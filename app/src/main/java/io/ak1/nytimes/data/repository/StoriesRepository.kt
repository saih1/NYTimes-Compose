package io.ak1.nytimes.data.repository

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import io.ak1.nytimes.data.local.AppDatabase
import io.ak1.nytimes.data.remote.ApiList
import io.ak1.nytimes.model.BaseData
import io.ak1.nytimes.model.Results
import io.ak1.nytimes.utility.LiveDataCollection
import io.ak1.nytimes.utility.NetworkState
import io.ak1.nytimes.utility.extractMessage
import io.ak1.nytimes.utility.resultsToBookmarks
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import javax.net.ssl.SSLException
import kotlin.coroutines.CoroutineContext


/**
 * Created by akshay on 14,November,2020
 * akshay2211@github.io
 */

/**
 * in [StoriesRepository] the lists retrieved from remote api is first stored in local database
 */
class StoriesRepository(
    private val db: AppDatabase,
    private val apiList: ApiList,
    private val coroutineContext: CoroutineContext
) {

    /**
     * Inserts the response into the database.
     */
    private fun insertResultIntoDb(section: String, body: BaseData?) {

        body!!.results.let { stories ->
            val list = stories.map {
                var height = 0
                var width = 0
                var urlMain = ""
                var urlThumb = ""
                if (!it.multimedia.isNullOrEmpty()) {
                    height = it.multimedia[0].height
                    width = it.multimedia[0].width
                    urlMain = it.multimedia[0].url ?: ""
                    urlThumb = it.multimedia[2].url ?: ""
                }


                Results(
                    it.title,
                    it.url,
                    it.published_date,
                    urlThumb,
                    urlMain,
                    section,
                    height,
                    width,
                    it.byline,
                    it.des_facet.joinToString { it },
                    it.abstract_text
                )
            }
            db.runInTransaction {
                CoroutineScope(this.coroutineContext).launch {
                    Log.e("list", "-> ${list[0].des_facet}")
                    db.resultsDao().insert(list)
                }
            }
        }
    }

    suspend fun deleteStories(type: String) = db.resultsDao().deleteBySectionType(type)
    fun getLocalStory(postId: Int): LiveData<Results> = db.resultsDao().getStoriesById(postId)

    fun getStories(type: String): LiveDataCollection<Results> {
        Log.e("retrieving", "stories for $type")
        val dao = db.resultsDao()
        val networkState = mutableStateOf(NetworkState.LOADING)
        CoroutineScope(this.coroutineContext).launch {
            if (dao.getCount(type) == 0) {
                // networkState.value = (NetworkState.LOADING)
                try {
                    val response = apiList.getStories(type)
                    if (!response.isSuccessful) {
                        val error = response.errorBody()
                        networkState.value = (NetworkState.error(error?.extractMessage()))
                        return@launch
                    }
                    insertResultIntoDb(type, response.body())
                    //  networkState.value = (NetworkState.LOADED)
                } catch (e: SSLException) {
                    e.printStackTrace()
                    // networkState.value =
                    //     (NetworkState.error(context.resources.getString(R.string.system_call_error)))
                } catch (e: UnknownHostException) {
                    e.printStackTrace()
                    //  networkState.value =
                    //      (NetworkState.error(context.resources.getString(R.string.internet_error)))
                } catch (e: Exception) {
                    e.printStackTrace()
                    // networkState.value = (NetworkState.error(e.localizedMessage))
                }
            }
        }

        return LiveDataCollection(
            pagedList = dao.storiesByType(type),
            networkState2 = networkState
        )
    }

    fun storeBookMark(results: Results, result: (Boolean) -> Unit) {
        CoroutineScope(this.coroutineContext).launch {
            val bookmark = db.bookmarksDao().getBookmarksById(results.id)
            if (bookmark != null) {
                db.resultsDao().insert(results.apply { bookmarked = false })
                db.bookmarksDao().deleteById(results.id)
                result(false)
            } else {
                db.resultsDao().insert(results.apply { bookmarked = true })
                db.bookmarksDao().insert(results.resultsToBookmarks())
                result(true)
            }
        }
    }

}


