package com.richard.dev.common

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.richard.ilbrary.compose.widget.FButton
import com.richard.ilbrary.compose.widget.TipDialog
import com.richard.ilbrary.compose.widget.data.DialogButton
import com.richard.ilbrary.compose.widget.util.defaultDialogWidth
import com.richard.library.basic.basic.BasicDialog

/**
 * @author: Richard
 * @createDate: 2026/5/25 10:48
 * @version: 1.0
 * @description: none
 */
class TestKTDialog(context: Context) : BasicDialog(context) {

    override fun initLayoutView() {
        setContent {
            AppNavHost()
        }
    }

    override fun initData() {
    }

    override fun bindListener() {

    }
}

// 路由页面定义
sealed class NavRoute(val route: String) {
    object Home : NavRoute("home")
    object Detail : NavRoute("detail")
    object Mine : NavRoute("mine")
}

@Composable
fun AppNavHost() {
    // 1. 创建导航控制器
    val navController: NavHostController = rememberNavController()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorResource(R.color.bg)
    ) {
        // 2. NavHost 主体
        NavHost(
            navController = navController,
            startDestination = NavRoute.Home.route, // 启动页
            modifier = Modifier.fillMaxSize()
        ) {
            // ----------- 页面注册 -----------
            composable(NavRoute.Home.route) {
                HomePage(navController)
            }

            composable(NavRoute.Detail.route) {
                DetailPage(navController)
            }

            composable(NavRoute.Mine.route) {
                MinePage(navController)
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            Log.d("testtt", "onDispose-> AppNavHost")
        }
    }
}

// 首页
@Composable
fun HomePage(navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        val showDialog = remember { mutableStateOf(false) }

        FButton(text = "弹出确认框", height = 50.dp, isOutlinedButton = true) {
            showDialog.value = true
        }

        TipDialog(
            modifier = Modifier.defaultDialogWidth(),
            show = showDialog,
            title = "操作提示",
            titleIconId = R.mipmap.ic_tree_checked,
            message = "确定要执行该操作吗？",
            buttonList = listOf(
                DialogButton(text = "取消", isOutline = true),
                DialogButton(text = "确定")
            )
        )

        Button(onClick = {
            // 跳转到详情页
            navController.navigate(NavRoute.Detail.route)
        }) {
            Text(text = "去详情页")
        }
    }

    // --------------------------
    // 🔥 Compose 页面生命周期监听
    // --------------------------
    DisposableEffect(Unit) {
        onDispose {
            Log.d("testtt", "onDispose-> HomePage")
        }
    }
}

// 详情页
@Composable
fun DetailPage(navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "详情页")

        Button(onClick = {
            navController.navigate(NavRoute.Mine.route)
        }) {
            Text(text = "去我的页")
        }

        Button(onClick = {
            navController.popBackStack()
        }) {
            Text(text = "返回")
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            Log.d("testtt", "onDispose-> DetailPage")
        }
    }
}


// 我的
@Composable
fun MinePage(navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "我的")

        Button(onClick = {
            // 返回上一页（触发销毁）
            navController.popBackStack()
        }) {
            Text(text = "返回")
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            Log.d("testtt", "onDispose-> MinePage")
        }
    }
}