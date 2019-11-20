package com.treasure.loopang

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.ListView
import com.treasure.loopang.adapter.CommunityFeedItemAdapter
import com.treasure.loopang.listitem.CommunitySongItem
import kotlinx.android.synthetic.main.community_feed.*
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import com.treasure.loopang.adapter.CommunityFeedCategoryAdapter
import com.treasure.loopang.communication.ASyncer
import com.treasure.loopang.communication.MusicListClass
import com.treasure.loopang.listitem.CommunityFeedCategoryItem
import kotlinx.android.synthetic.main.activity_community.*
import java.util.*
import kotlin.collections.ArrayList

class CommunityFeedFragment : androidx.fragment.app.Fragment() {
    private var likeList : MutableList<MusicListClass> = MutableList<MusicListClass>(0, { MusicListClass () })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.treasure.loopang.R.layout.community_feed,container,false);
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       // Log.d("wwwwwwwwwwww","들어왔다.")

        if((activity as CommunityActivity).isTrackFragOpen == false && (activity as CommunityActivity).isCategorySelected == false) {
            communityFeedListView.visibility = View.GONE
            communityFeedCategoryListView.visibility = View.VISIBLE
            CategotyTextView.visibility=View.INVISIBLE
        }
        val CategoryAdapter: CommunityFeedCategoryAdapter = CommunityFeedCategoryAdapter()

        communityFeedCategoryListView.adapter = CategoryAdapter
        CategoryAdapter.addItem("The Newest 5")
        CategoryAdapter.addItem("Liked Top 5")
        CategoryAdapter.addItem("Download Top 5")

        communityFeedCategoryListView.onItemClickListener = AdapterView.OnItemClickListener { parent, v, position, id ->
            val item = parent.getItemAtPosition(position) as CommunityFeedCategoryItem
            (activity as CommunityActivity).isCategorySelected = true
            communityFeedListView.visibility = View.VISIBLE
            communityFeedCategoryListView.visibility = View.GONE
            CategotyTextView.visibility=View.VISIBLE

            val FeedAdapter : CommunityFeedItemAdapter = CommunityFeedItemAdapter()
            communityFeedListView.adapter = FeedAdapter

            CategotyTextView.text=item.categoryName
            likeList.addAll((activity as CommunityActivity).likeList)
            if(item.categoryName == "The Newest 5") {
                (activity as CommunityActivity).connector?.feedResult?.recent_musics?.forEach { FeedAdapter.addItem(it,likeList) }
            } else if(item.categoryName== "Liked Top 5") {
                (activity as CommunityActivity).connector?.feedResult?.likes_top?.forEach { FeedAdapter.addItem(it,likeList) }
            } else if(item.categoryName == "Download Top 5"){ (
                    activity as CommunityActivity).connector?.feedResult?.download_top?.forEach { FeedAdapter.addItem(it,likeList) }
            }
        }

        communityFeedListView.onItemClickListener = AdapterView.OnItemClickListener { parent, v, position, id ->
            val itt = parent.getItemAtPosition(position) as MusicListClass
            activity!!.TrackFrame.visibility = View.VISIBLE
            (activity as CommunityActivity).onFragmentChangedtoTrack(itt)
        }
    }
}