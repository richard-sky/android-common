package com.richard.dev.common.activity

import android.content.Context
import android.content.Intent
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.richard.dev.common.R
import com.richard.ilbrary.compose.widget.FButton
import com.richard.ilbrary.compose.widget.PageScaffold
import com.richard.library.basic.basic.BasicActivity
import com.richard.library.net.websocket.WebSocketClient

/**
 * @author: Richard
 * @createDate: 2026/6/25 12:02
 * @version: 1.0
 * @description: Websocket测试页面
 */
class TestWebsocketActivity : BasicActivity() {

    lateinit var webSocketClient: WebSocketClient

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, TestWebsocketActivity::class.java))
        }
    }

    override fun initLayoutView() {
        setContent {
            PageScaffold(titleText = "Websocket Demo") {
                UI()
            }
        }
    }

    override fun initData() {
        webSocketClient = WebSocketClient.create()
            .url("wss://dds.dui.ai/dds/v3/V1.0")
            .build()
    }

    override fun bindListener() {
    }

    @Composable
    private fun UI() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensionResource(R.dimen.content_padding)),
        ) {

            FButton(text = "连接", modifier = Modifier.fillMaxWidth()) {
                connect()
            }


        }
    }

    private fun connect() {

    }
}