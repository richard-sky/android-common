package com.richard.dev.common.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.richard.dev.common.R;
import com.richard.dev.common.databinding.ActivityCron4jTaskBinding;
import com.richard.library.basic.basic.BasicBindingActivity;
import com.richard.library.context.util.LogUtil;

import it.sauronsoftware.cron4j.Scheduler;

/**
 * @author: Richard
 * @createDate: 2025/4/30 11:30
 * @version: 1.0
 * @description: Cron4J test 详见 <a href="http://www.sauronsoftware.it/projects/cron4j/manual.php">...</a>
 */
public class Cron4jActivity extends BasicBindingActivity<ActivityCron4jTaskBinding> {

    private final Scheduler scheduler = new Scheduler(); // 创建调度器

    public static void start(Context context) {
        context.startActivity(new Intent(context, Cron4jActivity.class));
    }

    @Override
    public void initLayoutView() {
        setContentView(R.layout.activity_cron4j_task);
    }

    @Override
    public void initData() {
        navigationbar.setVisibility(View.VISIBLE);
        navigationbar.setTitle("cron测试");
        navigationbar.setTitleTextViewShow(true);

        // 将自定义任务设置为Job
        scheduler.schedule("*/1 * * * *", new Runnable() {
            @Override
            public void run() {
                LogUtil.dTag("testtt", "-----------1------------>>>>>>>");
            }
        });

        scheduler.schedule("*/5 * * * *", new Runnable() {
            @Override
            public void run() {
                LogUtil.dTag("testtt", "------------2----------->>>>>>>");
            }
        });

    }

    @Override
    public void bindListener() {
        binding.btnStart.setOnClickListener(v -> {
            scheduler.start();
        });

        binding.btnStop.setOnClickListener(v -> {
            scheduler.stop();
        });
    }


//    cron4j的cron表达式最多只允许5个部分，每个部分用空格分隔开，从左至右分别表示“分”、“时”、“天”、“月”、“周”，具体规则如下：
//    * * * * * , 从左至右：
//
//    分：取值从 0 到 59
//    时：取值从 0 到 23
//    天：取值从 1 到 31，字母 L 可用于表示月的最后一天
//    月：取值从 1 到 12，可以用别名表示：jan、feb、mar、apr、may、jun、jul、aug、sep、oct、nov、dec
//    周：取值从 0 到 6，0表示周日，6表示周六，可以用别名表示：sun、mon、tue、wed、thu、fri、sat
//    以上5个部分的分、时、天、月、周又分别支持如下字符：
//    数字 n ：表示一个具体的时间点，例如 5 * * * * 表示 5 分这个时间点时执行
//    逗号 , ：表示指定多个数值，例如 3,5 * * * * 表示 3 和 5 分这两个时间点执行
//    减号 - ：表示范围，例如 1-3 * * * * 表示 1 分、2 分再到 3 分这三个时间点执行
//    星号 * ：表示每一个时间点，例如 * * * * * 表示每分钟执行
//    除号 / ：表示指定一个值的增加幅度。例如 */5表示每隔5分钟执行一次（序列：0:00, 0:05, 0:10, 0:15 等等）
}
