package com.treasure.loopang

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.treasure.loopang.listitem.setMetronome
import com.treasure.loopang.ui.toast
import kotlinx.android.synthetic.main.setting_item_back.*
import kotlinx.android.synthetic.main.setting_pcm.*
import kotlinx.android.synthetic.main.setting_pcm.view.*
import kotlinx.android.synthetic.main.setting_visualizer.*
import kotlinx.android.synthetic.main.setting_visualizer.view.*
import org.w3c.dom.Text
import java.util.zip.Inflater

class settingItemActivity : AppCompatActivity() {
    lateinit var settingItemNoticeFrag : settingItemNoticeFragment
    lateinit var settingItemMyMusicFrag : settingItemMyMusicFragment
    lateinit var settingItemHowToUse: settingItemHowToUse
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setting_item_back)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        val intent = getIntent()//데이터 수신
        val title = intent.extras!!.getString("title")
        settingItemNoticeFrag = settingItemNoticeFragment()
        settingItemMyMusicFrag = settingItemMyMusicFragment()
        settingItemHowToUse = settingItemHowToUse()
        //var itemInflater : LayoutInflater = (LayoutInflater.from(context).inflate(R.layout.setting_notice,null,false))

       // var setiingItemFrameLayout: FrameLayout = findViewById(R.id.settingItemFrameLayout)

        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        settingItemNameText.text = title

        when(title) {

            "Visualizer" -> {
                visibilityNotice.visibility = View.INVISIBLE
                visibilityVisualizer.visibility= View.VISIBLE
                visibilityPcm.visibility= View.INVISIBLE
                visibilityMymusic.visibility =View.INVISIBLE
                inflater.inflate(R.layout.setting_visualizer, null)
                var visualizerVersionNum : Int = 1
                btn_visualizer_ver1.setOnClickListener {
                    visualizerVersionNum = 1
                    visualizer_applied_info.text = "1"}
                btn_visualizer_ver2.setOnClickListener {
                    visualizerVersionNum = 2
                    visualizer_applied_info.text = "2"}
                }

            "Pcm" -> {
                visibilityNotice.visibility = View.INVISIBLE
               visibilityVisualizer.visibility= View.INVISIBLE
                visibilityPcm.visibility= View.VISIBLE
                visibilityMymusic.visibility =View.INVISIBLE

                inflater.inflate(R.layout.setting_pcm, null)
                var pcm : Int = 16
               btn_pcm16.setOnClickListener {
                   pcm = 16
                   pcm_applied_info.text = "16" }
                btn_pcm32.setOnClickListener {
                    pcm = 32
                    pcm_applied_info.text = "32" }
            }
            "도움말" ->{
                getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, 0, 0, R.anim.fade_out)
                    .replace(R.id.settingItemContainer, settingItemHowToUse)
                    .commit()
            }
            "My Music" ->{
                getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, 0, 0, R.anim.fade_out)
                    .replace(R.id.settingItemContainer, settingItemMyMusicFrag)
                    .commit()
            }
            "Notice" -> {
                settingItemNameText.text = "Notice"
                getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, 0, 0, R.anim.fade_out)
                    .replace(R.id.settingItemContainer, settingItemNoticeFrag)
                    .commit()
            }
        }

        go_back.setOnClickListener { finish() }
    }
   /* fun checkFragment(){
        for (fragment in supportFragmentManager.fragments) {
            if (fragment.isVisible) {
                when (title) {
                    "도움말" ->if( fragment is settingItemNoticeFragment || fragment is settingItemMyMusicFragment) {
                        getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.fade_in, 0, 0, R.anim.fade_out)
                            .replace(R.id.fragContainer, settingItemHowToUse)
                            .commit()
                    }
                    "My Music" ->  if( fragment is settingItemNoticeFragment || fragment is settingItemHowToUse){
                        //  settingItemView = inflater.inflate(R.layout.setting_my_music, null)
                        getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.fade_in, 0, 0, R.anim.fade_out)
                            .replace(R.id.fragContainer, settingItemMyMusicFrag)
                            .commit()
                    }
                    "Notice" -> if( fragment is settingItemHowToUse || fragment is settingItemMyMusicFragment){
                        //  settingItemView = inflater.inflate(R.layout.setting_notice, null)
                        settingItemNameText.text = "Notice"
                        getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.fade_in, 0, 0, R.anim.fade_out)
                            .replace(R.id.fragContainer, settingItemNoticeFrag)
                            .commit()
                    }
                }
            }
        }
    }*/
}