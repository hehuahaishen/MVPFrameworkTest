package com.shen.mvpframework.view;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.shen.mvpframework.factory.PresenterMvpFactory;
import com.shen.mvpframework.factory.PresenterMvpFactoryImpl;
import com.shen.mvpframework.presenter.BaseMvpPresenter;
import com.shen.mvpframework.proxy.BaseMvpProxy;
import com.shen.mvpframework.proxy.PresenterProxyInterface;

import java.util.List;

/**
 * 继承自AppCompatActivity的基类AbstractMvpAppCompatActivity
 * 使用代理模式来代理Presenter的创建、销毁、绑定、解绑以及Presenter的状态保存,其实就是管理Presenter的生命周期
 */
public abstract class AbstractMvpAppCompatActivity<V extends BaseMvpView, P extends BaseMvpPresenter<V>>
        extends AppCompatActivity implements PresenterProxyInterface<V,P> {

    public abstract int getContentLayout();
    public abstract void initView();
    public abstract void initListener();
    public abstract void initData();

    /** 调用onSaveInstanceState时存入Bundle的key */
    private static final String PRESENTER_SAVE_KEY = "presenter_save_key";
    /** 创建被代理对象,传入默认Presenter的工厂 */
    private BaseMvpProxy<V,P> mProxy = new BaseMvpProxy<>(PresenterMvpFactoryImpl.<V,P>createFactory(getClass()));

    public Context mContext;
    public Activity mActivity;
    public Toast mToast;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("perfect-mvp","V onCreate");
        Log.e("perfect-mvp","V onCreate mProxy = " + mProxy);
        Log.e("perfect-mvp","V onCreate this = " + this.hashCode());
        if(savedInstanceState != null){
            mProxy.onRestoreInstanceState(savedInstanceState.getBundle(PRESENTER_SAVE_KEY));
        }

        setContentView(getContentLayout());

        mContext = this;
        mActivity = this;

        initView();
        initListener();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("perfect-mvp","V onResume");
        mProxy.onResume((V) this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("perfect-mvp","V onDestroy = " + isChangingConfigurations());
        mProxy.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e("perfect-mvp","V onSaveInstanceState");
        outState.putBundle(PRESENTER_SAVE_KEY, mProxy.onSaveInstanceState());
    }

    /*-------------------- implements PresenterProxyInterface  start ---------------------*/
    @Override
    public void setPresenterFactory(PresenterMvpFactory<V, P> presenterFactory) {
        Log.e("perfect-mvp","V setPresenterFactory");
        mProxy.setPresenterFactory(presenterFactory);
    }

    @Override
    public PresenterMvpFactory<V, P> getPresenterFactory() {
        Log.e("perfect-mvp","V getPresenterFactory");
        return mProxy.getPresenterFactory();
    }

    @Override
    public P getMvpPresenter() {
        Log.e("perfect-mvp","V getMvpPresenter");
        return mProxy.getMvpPresenter();
    }
    /*-------------------- implements PresenterProxyInterface  end ---------------------*/


   /*-------------------------------- 显示 吐司 ---------------------------------*/

    /**
     * 如果 Toast对象存在"正在显示"      			<br>
     * 就"不等其显示完"再"显示另一个Toast"			<br>
     * 直接修改"toast内部文本"						<br>
     *
     * @param text 待显示的文字
     */
    public void showToast(String text) {
        // 判断程序是否在前台运行 如果程序是在后台运行 不显示toast
        if (!isTopActivity()) {
            return;
        }
        if (mToast != null) {
            mToast.setText(text);
        } else {
            mToast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        }
        //mToast.setGravity(Gravity.CENTER, 0, 0);	// 这里显示在中间 -- 可以去掉,就是默认位置
        mToast.show(); 								// 显示toast信息
    }



    /**
     * 程序是否正在前台运行
     *
     * @return
     */
    public boolean isTopActivity() {
		/*
		 * System.out.println("**********************top packageName:" +
		 * getInstance().getPackageName());
		 */
        ActivityManager activityManager = (ActivityManager) getApplication().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
        if (tasksInfo.size() > 0) {
			/*
			 * System.out.println("*********************curr packageName:" +
			 * tasksInfo.get(0).topActivity.getPackageName());
			 */
            // 应用程序位于堆栈的顶层
            if (getApplication().getPackageName().equals(tasksInfo.get(0).topActivity.getPackageName())) {
                return true;
            }
        }
        return false;
    }

}
