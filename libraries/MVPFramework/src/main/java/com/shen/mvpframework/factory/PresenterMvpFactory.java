package com.shen.mvpframework.factory;

import com.shen.mvpframework.presenter.BaseMvpPresenter;
import com.shen.mvpframework.view.BaseMvpView;

/**
 * Presenter工厂接口
 */
public interface PresenterMvpFactory<V extends BaseMvpView, P extends BaseMvpPresenter<V>> {

    /**
     * 创建Presenter的接口方法
     * @return 需要创建的Presenter
     */
    P createMvpPresenter();
}
