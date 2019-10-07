package com.treasure.loopang

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.activity_community.*
import kotlinx.android.synthetic.main.community_feed_item.*
import kotlinx.android.synthetic.main.community_track.*
import kotlinx.android.synthetic.main.community_track.view.*
import kotlinx.android.synthetic.main.setting_item_back.view.*

class CommunityTrackFragment: androidx.fragment.app.Fragment() {
    var downloadNum : Int = 0
    var heartClikedNum : Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.treasure.loopang.R.layout.community_track,container,false);
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var heartState : Boolean = false
        var StatePlaying : Boolean = false

        var trackName : String = "TrackName" //변수들 나중에 다 change
        Track_trackName.setText(trackName)

        var songInfoDate : String = "2019-09-09" //이건 String이 맞는지도 모르겠다 일단 이렇게 해놓자 ^^
        trackInfoDate.setText(songInfoDate)

        TrackInfoTextView.isEnabled = false
        trackHeartClikedNum.setText(heartClikedNum.toString())


        var songMasteruserNickName : String = "UserName" //변수들 나중에 다 change
        Track_artistName.setText(songMasteruserNickName)
        var presentuserNickname : String = "UserName2" //변수들 나중에 다 change
        trackInfoTextEdit.setEnabled(false);

        if(songMasteruserNickName == presentuserNickname) {
            trackInfoTextEdit.setEnabled(true); //사용자와 노래주인이 같으면 터치해서 info바꿀 수 있음

            trackInfoTextEdit.addTextChangedListener(object  : TextWatcher{
                override fun afterTextChanged(edit: Editable) {}
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    // var TrackInfo : String? = null 로 아이템 널어주기 ㅇㅇ
                }
            })
        }

        heartButton.setOnClickListener {
            if(heartState == false) {
                heartState = true
                heartClikedNum += 1
                heartButton.setImageDrawable(getResources().getDrawable(R.drawable.trackicon_heart_clicked))
            }
            else {
                heartState = false
                heartClikedNum -=1
                heartButton.setImageDrawable(getResources().getDrawable(R.drawable.trackicon_heart))
            }
        }

        downloadButton.setOnClickListener {
            //사용자의 recording item으로 song이 들어가게 하는 기능 추가
            Log.d("download","download")
            playNumText.setText(downloadNum.toString())
            downloadNum++
        }

        Track_btn_play.setOnClickListener {
            if(StatePlaying == false) {
                Track_btn_play.setImageDrawable(getResources().getDrawable(R.drawable.trackicon_pause))

                TrackBtnReplay.visibility = View.VISIBLE
                StatePlaying =true
                Log.d("pause btn replay o","pause btn > replay o")
            }
            else { //statePlaying == true
                Track_btn_play.setImageDrawable(getResources().getDrawable(R.drawable.trackicon_play))
                TrackBtnReplay.visibility = View.GONE
                StatePlaying = false
                Log.d("playBtn > replay x","playBtn > replay x")
            }
        }

        TrackBtnReplay.setOnClickListener { // 음원의 재생을 처음으로 돌리기 기능
            Log.d("replay Btn" , "replay btn")
        }

        track_btn_back.setOnClickListener {
            activity!!.TrackFrame.visibility = View.GONE
            val fragmentManager = activity!!.supportFragmentManager
            fragmentManager.beginTransaction().remove(this).commit()
            fragmentManager.popBackStack()
        }
    }
}