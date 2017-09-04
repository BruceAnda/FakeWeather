package com.liyu.fakeweather.ui;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.liyu.fakeweather.R;
import com.liyu.fakeweather.http.ApiFactory;
import com.liyu.fakeweather.model.WeatherCityBean;
import com.liyu.fakeweather.ui.base.BaseActivity;
import com.liyu.fakeweather.utils.ToastUtil;

import org.litepal.crud.DataSupport;

import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by liyu on 2017/8/31.
 */

public class SplashActivity extends BaseActivity {

    LottieAnimationView lottieAnimationView;
    TextView tvInfo;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_welcome;
    }

    @Override
    protected int getMenuId() {
        return 0;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        lottieAnimationView = (LottieAnimationView) findViewById(R.id.animation_view);
        tvInfo = (TextView) findViewById(R.id.tvInfo);
    }

    @Override
    protected void loadData() {
        if (DataSupport.count(WeatherCityBean.class) <= 0) {
            ApiFactory
                    .getAppController()
                    .getWeatherCityList()
                    .subscribeOn(Schedulers.io())
                    .map(new Func1<List<WeatherCityBean>, List<WeatherCityBean>>() {
                        @Override
                        public List<WeatherCityBean> call(List<WeatherCityBean> weatherCityBeen) {
                            DataSupport.saveAll(weatherCityBeen);
                            return weatherCityBeen;
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<List<WeatherCityBean>>() {
                        @Override
                        public void onCompleted() {
                            lottieAnimationView.cancelAnimation();
                            lottieAnimationView.setAnimation("ProgressSuccess.json");
                            lottieAnimationView.loop(false);
                            lottieAnimationView.playAnimation();
                            lottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                    SplashActivity.this.finish();
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            });
                        }

                        @Override
                        public void onError(Throwable e) {
                            lottieAnimationView.cancelAnimation();
                            lottieAnimationView.setAnimation("x_pop.json");
                            lottieAnimationView.playAnimation();
                            ToastUtil.showShort(e.getMessage() + e.getCause());
                            tvInfo.setText("更新失败，请检查网络状态！");
                        }

                        @Override
                        public void onNext(List<WeatherCityBean> weatherCityBeen) {

                        }
                    });
        } else {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            SplashActivity.this.finish();
        }
    }
}