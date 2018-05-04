package com.shen.mvpframework.view;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.shen.mvpframework.factory.PresenterMvpFactory;
import com.shen.mvpframework.factory.PresenterMvpFactoryImpl;
import com.shen.mvpframework.presenter.BaseMvpPresenter;
import com.shen.mvpframework.proxy.BaseMvpProxy;
import com.shen.mvpframework.proxy.PresenterProxyInterface;

import java.util.List;

/**
 * 继承Fragment的MvpFragment基类
 */
public abstract class AbstractFragment<V extends BaseMvpView, P extends BaseMvpPresenter<V>>
        extends Fragment implements PresenterProxyInterface<V, P> {

    public abstract int getContentLayout();
    public abstract void initView(View view);
    public abstract void initListener();
    public abstract void initData();

    /** 调用onSaveInstanceState时存入Bundle的key */
    private static final String PRESENTER_SAVE_KEY = "presenter_save_key";
    /** 创建被代理对象,传入默认Presenter的工厂 */
    private BaseMvpProxy<V, P> mProxy = new BaseMvpProxy<>(PresenterMvpFactoryImpl.<V, P>createFactory(getClass()));

    public Context mContext;
    public Activity mActivity;
    public Toast mToast;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            mProxy.onRestoreInstanceState(savedInstanceState);
        }
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getContentLayout(), container, false);

        mContext = getContext();
        mActivity = getActivity();

        initView(view);
        initListener();
        initData();

        view.setFocusable(true);            //这个和下面的这个命令必须要设置了，才能监听back事件。
        view.setFocusableInTouchMode(true);
        view.setOnKeyListener(mKeyListener);

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        mProxy.onResume((V) this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mProxy.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle(PRESENTER_SAVE_KEY, mProxy.onSaveInstanceState());
    }



    /*-------------------- implements PresenterProxyInterface  start ---------------------*/

    /**
     * 可以实现自己PresenterMvpFactory工厂
     *
     * @param presenterFactory PresenterFactory类型
     */
    @Override
    public void setPresenterFactory(PresenterMvpFactory<V, P> presenterFactory) {
        mProxy.setPresenterFactory(presenterFactory);
    }


    /**
     * 获取创建Presenter的工厂
     *
     * @return PresenterMvpFactory类型
     */
    @Override
    public PresenterMvpFactory<V, P> getPresenterFactory() {
        return mProxy.getPresenterFactory();
    }

    /**
     * 获取Presenter
     * @return P
     */
    @Override
    public P getMvpPresenter() {
        return mProxy.getMvpPresenter();
    }
    /*-------------------- implements PresenterProxyInterface  end ---------------------*/


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
            mToast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
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
        ActivityManager activityManager = (ActivityManager) mActivity.getApplication().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
        if (tasksInfo.size() > 0) {
			/*
			 * System.out.println("*********************curr packageName:" +
			 * tasksInfo.get(0).topActivity.getPackageName());
			 */
            // 应用程序位于堆栈的顶层
            if (mActivity.getApplication().getPackageName().equals(tasksInfo.get(0).topActivity.getPackageName())) {
                return true;
            }
        }
        return false;
    }



    private View.OnKeyListener mKeyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View view, int i, KeyEvent keyEvent) {

            return false;
        }
    };
}
