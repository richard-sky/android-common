package com.richard.library.simplerx;

import android.os.Looper;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import java.util.Objects;

import autodispose2.AutoDispose;
import autodispose2.AutoDisposePlugins;
import autodispose2.ScopeProvider;
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.exceptions.UndeliverableException;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * <pre>
 * Description : 针对Rx调用简化的封装
 * Author : admin-richard
 * Date : 2019-06-21 17:55
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2019-06-21 17:55     admin-richard         new file.
 * </pre>
 * <p>
 * Schedulers.computation(): 适合运行在密集计算的操作，大多数异步操作符使用该调度器。它由一个有界的线程池作为支撑，线程的最大数量就是可用的处理器数
 * Schedulers.io():适合运行I/0和阻塞操作.
 * Schedulers.single():适合需要单一线程的操作
 * Schedulers.trampoline(): 适合需要顺序运行的操作
 * AndroidSchedulers.mainThread()：适合Android中主线程的操作
 * <p>
 * subscribeOn：指定生产事件的线程池(若多次调用，仅第一次生效，后续调用无效),create方法中执行的线程
 * observeOn：影响调用点之后的所有操作（可多次调用，每次切换线程）
 * unsubscribeOn：指定取消订阅（unsubscribe） 时的资源清理线程（如关闭数据库连接、取消网络请求）
 * 仅影响取消订阅时的清理操作，不影响事件生产或消费线程。
 * 需在订阅后通过Subscription.unsubscribe()触发。
 */
public final class XObservable<T> {

    private Observable<T> observable;
    private ScopeProvider scopeProvider;
    private static OnGlobalError onGlobalError;

    static {
        //RxJava全局配置
        if (RxJavaPlugins.getErrorHandler() == null) {
            RxJavaPlugins.setErrorHandler(e -> {
                if (e instanceof UndeliverableException) {
                    Log.e("RxJava", "RxJava全局错误", e);
                    if (onGlobalError != null) {
                        onGlobalError.onRxJavaUndeliverableException(e);
                    }
                }
            });
        }

        //设置订阅发生在作用域外异常处理
        if (AutoDisposePlugins.getOutsideScopeHandler() == null) {
            AutoDisposePlugins.setOutsideScopeHandler(e -> {
                Log.e("AutoDispose", "订阅发生在作用域外", e);
                if (onGlobalError != null) {
                    onGlobalError.onAutoDisposeOutsideScopeException(e);
                }
            });
        }
    }

    /**
     * 设置全局异常回调
     */
    public static void setOnGlobalError(OnGlobalError onGlobalError) {
        XObservable.onGlobalError = onGlobalError;
    }

    /**
     * 全局异常回调
     */
    public interface OnGlobalError {
        /**
         * 当RxJava 发生UndeliverableException异常时回调
         */
        void onRxJavaUndeliverableException(Throwable e);

        /**
         * 当AutoDispose 发生OutsideScopeException异常时回调
         */
        void onAutoDisposeOutsideScopeException(Throwable e);
    }

    private XObservable(Observable<T> observable) {
        this.observable = observable;
    }

    public static <T> XObservable<T> create(XObservableOnSubscribe<T> source) {
        return new XObservable<T>(Observable.create(source));
    }

    public static <T> XObservable<T> create(Observable<T> observable) {
        return new XObservable<>(observable);
    }

    /**
     * 绑定生命周期
     * 注意：只能在主线程中绑定，其他线程绑定无效(只能在toXxxSubscribe()方法前一步链式调用)
     */
    public XObservable<T> bindLife(Fragment fragment) {
        if (fragment == null) {
            return this;
        }
        return this.bindLife(fragment.getViewLifecycleOwner());
    }

    /**
     * 绑定生命周期
     * 注意：只能在主线程中绑定，其他线程绑定无效(只能在toXxxSubscribe()方法前一步链式调用)
     */
    public XObservable<T> bindLife(LifecycleOwner lifecycleOwner) {
        if (lifecycleOwner == null) {
            return this;
        }
        return this.bindLife(AndroidLifecycleScopeProvider.from(lifecycleOwner, Lifecycle.Event.ON_DESTROY));
    }

    /**
     * 绑定生命周期
     * 注意：只能在主线程中绑定，其他线程绑定无效
     */
    public XObservable<T> bindLife(ScopeProvider scopeProvider) {
        if (Looper.getMainLooper().getThread() != Thread.currentThread()) {
            return this;
        }
        this.scopeProvider = scopeProvider;
        return this;
    }

    /**
     * Observable转换器
     *
     * @param converter 转换器
     * @param <R>       返回结果
     */
    public <R> XObservable<R> to(XObservableConverter<T, R> converter) {
        return Objects.requireNonNull(converter, "converter is null").converter(this);
    }

    /**
     * 串联RxJava
     * 处理线程：Android 主线程
     * 回调线程：Android 主线程
     *
     * @param observer 回调 可为null
     */
    public void toSyncUISubscribe(Observer<T> observer) {
        observable = observable.subscribeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread());
        this.toSubscribe(observer);
    }

    /**
     * 串联RxJava
     * 处理线程：同调用源线程
     * 回调线程：同调用源线程
     *
     * @param isCanThrowError 是否可以跑出异常（true：抛出异常、false：返回null）
     */
    public T toSyncSubscribe(boolean isCanThrowError) {
        if (isCanThrowError) {
            return observable.blockingSingle();
        }

        try {
            return observable.blockingSingle();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 串联RxJava
     * 处理线程：同调用源线程
     * 回调线程：同调用源线程
     *
     * @param observer 回调 可为null
     */
    public void toSyncSubscribe(Observer<T> observer) {
        this.toSubscribe(observer);
    }

    /**
     * 串联RxJava
     * 处理线程：子(异步)线程
     * 回调线程：Android主线程
     *
     * @param observer 回调 可为null
     */
    public void toAsyncSubscribe(Observer<T> observer) {
        observable = observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        this.toSubscribe(observer);
    }

    /**
     * 串联RxJava
     * 处理线程：子(异步)线程
     * 回调线程：子(异步)线程
     */
    public void toAsyncBackSubscribe() {
        this.toAsyncBackSubscribe(null);
    }

    /**
     * 串联RxJava
     * 处理线程：子(异步)线程
     * 回调线程：子(异步)线程
     *
     * @param observer 回调 可为null
     */
    public void toAsyncBackSubscribe(Observer<T> observer) {
        observable = observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
        this.toSubscribe(observer);
    }

    /**
     * 串联RxJava(单线程串行执行事件)
     * 处理线程：子(异步)线程
     * 回调线程：Android 主线程
     *
     * @param observer 回调 可为null
     */
    public void toFIFOSubscribe(Observer<T> observer) {
        observable = observable.subscribeOn(Schedulers.single())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        this.toSubscribe(observer);
    }

    /**
     * 开始订阅
     */
    public void toSubscribe(Observer<T> observer) {
        if (observer == null) {
            observer = this.createXSubscribe();
        }

        if (scopeProvider != null) {
            observable.to(AutoDispose.autoDisposable(scopeProvider)).subscribe(observer);
            return;
        }
        observable.subscribe(observer);
    }

    /**
     * 指定执行订阅操作的调度线程
     *
     * @param scheduler 调度者
     */
    public XObservable<T> subscribeOn(Scheduler scheduler) {
        this.observable = this.observable.subscribeOn(scheduler);
        return this;
    }

    /**
     * 指定执行取消订阅操作的调度线程
     *
     * @param scheduler 调度者
     */
    public XObservable<T> unsubscribeOn(Scheduler scheduler) {
        this.observable = this.observable.unsubscribeOn(scheduler);
        return this;
    }

    /**
     * 指定通知观察者的调度线程
     *
     * @param scheduler 调度者
     */
    public XObservable<T> observeOn(Scheduler scheduler) {
        this.observable = this.observable.observeOn(scheduler);
        return this;
    }

    /**
     * RxJava操作方法调用
     *
     * @param connector RxJava 方法调用
     */
    public XObservable<T> with(RxWith<T> connector) {
        this.observable = connector.with(this.observable);
        return this;
    }

    /**
     * 创建空实现观察者
     */
    private XSubscribe<T> createXSubscribe() {
        return new XSubscribe<T>() {
            @Override
            public void onXNext(T data) {

            }
        };
    }
}


//        记录RxJava操作符，方便查询（2.2.2版本）
//        英文文档地址：http://reactivex.io/documentation/operators.html
//
//        一、创建操作（Observable操作符）
//        二、变换操作
//        三、过滤操作
//        四、组合操作
//        五、错误处理
//        六、辅助操作
//        七、条件和布尔操作
//        八、算术和聚合操作
//        九、连接操作
//        十、转换操作
//        十一、背压操作符
//
//
//        一、创建操作（Observable操作符）
//        Create：创建一个被观察者Observable实例
//        Defer：在观察者订阅之前不创建这个Observable，为每一个观察者创建一个新的Observable
//        Empty/Never：创建行为受限的特殊Observable
//        From：将其它的对象或数据结构转换为Observable
//        Interval：创建一个特定时间间隔的整数序列Observable，运行在Schedulers.computation()线程内
//        Just：将一个对象或者对象集合转换为一个会依次发射这些对象的Observable
//        Range：创建发射指定范围的整数序列的Observable
//        Repeat：作用于Observable，用于创建一个重复地发出特定的项或项序列的Observable
//        Start：创建一个开始发射前返回指定值的Observable
//        Timer：在指定时间后发射一个0，运行在Schedulers.computation()线程内
//

//        二、变换操作
//        对Observable发射的数据进行处理，转化为我们想要的格式。
//        Buffer：缓存。定期从Observable收集数据到一个集合中，然后把集合打包发射，而不是一次发射一个 。
//        FlatMap：将Observable发射的数据变换为Observables集合，然后将这些Observable发射的数据扁平映射到一个单独的Observable（嵌套的数据结构展开的过程）。
//        GroupBy：分组，将原来的Observable分拆为Observable集合。将原始Observable发射的数据按Key分组，每一个Observable发射一组不同的数据。
//        Map：映射。通过对序列的每一项都应用一个函数变换Observable发射的数据。
//        Scan：扫描， 连续地对数据序列的每一项应用一个函数，然后连续发射结果，每一项结果基于之前的结果。累加器函数。
//        Window：窗口，定期将来自Observable的数据分拆成一些Observable窗口，然后发射这些窗口，而不是每次发射一项。似于Buffer，但Buffer发射的是数据，Window发射的是Observable，每一个Observable发射原始Observable的数据的一个子集。
//        toList：将一个Observable转换为一个List。
//
//
//        三、过滤操作
//        滤掉数据集合中我们不想要的数据。用于从Observable发射的数据中进行选择。
//        Debounce：只有在空闲了一段时间后才发射数据，通俗的说，就是如果一段时间没有操作，就执行一次操作。还可以根据一个函数来进行限流。这个函数的返回值是一个临时Observable，如果源Observable在发射一个新的数据的时候，上一个数据根据函数所生成的临时Observable还没有结束,没有调用onComplete，那么上一个数据就会被过滤掉。如果是最后一个,还是会发射。
//        Distinct：去重，过滤掉所有的重复数据项。
//        ElementAt：取值，取特定位置的数据项。
//        Filter：过滤发射。
//        First：取满足条件的第一个，无满足条件数据抛出异常。可以使用takeFisrt()，仅会调动onComplete。有null数据时会抛空指针异常，要判空处理。只取满足条件的第一个数据，可以和BlockingObservable连用。可以Observable.toBlocking或者BlockingObservable.from方法来将一个Observable对象转化为BlockingObservable对象。
//        IgnoreElements：忽略所有的数据，只保留终止通知(onError或onCompleted)。
//        Last：只发射最后一条数据。
//        Sample：取样，发射在周期时间间隔内发射的最新数据。
//        Skip：跳过前面的若干项数据。
//        SkipLast：跳过后面的若干项数据。
//        Take：只保留前面的若干项数据。
//        TakeLast：只保留后面的若干项数据。
//        throttleWithTimeout：源Observable每次发射出来一个数据后就会进行计时,如果在设定好的时间结束前源Observable有新的数据发射出来，这个数据就会被丢弃，同时重新开始计时。
//
//
//        四、组合操作
//        And/Then/When：通过模式(And条件)和计划(Then次序)组合两个或多个Observable发射的数据集。
//        CombineLatest：当两个Observables中的任何一个发射了一个数据时，通过一个指定的函数组合每个Observable发射的最新数据（一共两个数据），然后发射这个函数的结果。必须满足两个条件：1)所有的Observable都发射过数据； 2)满足条件1的时候任何一个Observable发射一个数据，就将所有Observable最新发射的数据按照提供的函数组装起来发射出去。 在这两个条件下,可能会忽略掉一些发射的数据。
//        Join：无论何时，如果一个Observable发射了一个数据项，只要在另一个Observable发射的数据项定义的时间窗口内， 就将两个Observable发射的数据合并发射。参数说明：1)源Observable所要组合的目标Observable；2)一个函数，就接收从源Observable发射来的数据，并返回一个Observable，这个Observable的生命周期决定了源Observable发射出来数据的有效期；3)一个函数，就接收从目标Observable发射来的数据，并返回一个Observable，这个Observable的生命周期决定了目标Observable发射出来数据的有效期；4)一个函数，接收从源Observable和目标Observable发射来的数据，并返回最终组合完的数据。
//        Merge：将两个Observable发射的数据组合并成一个。Merge可能会让合并的Observables发射的数据交错（可以使用Concat操作符，不会让数据交错，它会按顺序一个接着一个发射多个Observables的发射物）。
//        StartWith：在发射原来的Observable的数据序列之前，先发射一个指定的数据序列或数据项。在数据序列的开头插入一条指定的项，你也可以传递一个Observable给startWith，它会将那个Observable的发射物插在原始Observable发射的数据序列之前。这可以看作是Concat的反转。
//        Switch：将一个发射Observable序列的Observable转换为发出这些可观察对象中最近发出的项的单个可观察对象Observable：它逐个发射那些Observable最近发射的数据，用来将一个发射多个小Observable的源Observable转化为一个Observable，然后发射这多个小Observable所发射的数据。需要注意的就是，如果一个小的Observable正在发射数据的时候，源Observable又发射出一个新的小Observable，则前一个Observable发射的数据会被抛弃，直接发射新的小Observable所发射的数据。
//        Zip：打包，使用一个指定的函数将多个Observable发射的数据组合在一起，然后将这个函数的结果作为单项数据发射。Zip操作符将多个Observable发射的数据按顺序组合起来，每个数据只能组合一次，而且都是有序的。最终组合的数据的数量由发射数据最少的Observable来决定。
//
//
//        五、错误处理
//        这些操作符用于从错误通知中恢复
//        Catch：捕获，继续序列操作，将错误替换为正常的数据，从onError通知中恢复。
//        onErrorReturn：当发生错误的时候，让Observable发射一个预先定义好的数据并正常地终止，不会抛出异常 onErrorResumeNext。当发生错误的时候，由另外一个Observable来代替当前的Observable并继 续发射数据 onExceptionResumeNext。类似于OnErrorResume，不同之处在于其会对onError抛出的数据类型做 判断，如果是Exception，也会使用另外一个Observable代替原Observable继续发射数据，否则会将错误分发给Subscriber。
//        Retry：重试，如果Observable发射了一个错误通知，重新订阅它，期待它正常终止。Retry操作符在发生错误的时候会重新进行订阅，而且可以重复多次，所以发射的数据可能会产生重复。如果重复指定次数还有错误的话就会将错误返回给观察者，会调用onError。
//        retryWhen：当错误发生时，retryWhen会接收onError的throwable作为参数，并根据定义好的函数返回一个Observable，如果这个Observable发射一个数据，就会重新订阅。需要注意的是使用retryWhen的时候，因为每次重新订阅都会产生错误，所以作为参数的obserbvable会不断地发射数据，使用zipWith操作符可以限制重新订阅的次数，否则会无限制地重新订阅。会正常结束，调用onCompleted。
//
//
//        六、辅助操作
//        Delay：延迟一段时间发射结果数据。
//        DelaySubscription：延迟注册到Observer上。
//        Do：给Observable的生命周期的各个阶段加上一系列的回调监听，当Observable执行到这个阶段的时候，这些回调就会被触发。
//        doOnEach：Observable每发射一个数据的时候就会触发这个回调，不仅包括onNext还包括onError和onCompleted。
//        doOnNext：只有onNext的时候才会被触发。doOnSubscribe，doOnUnSubscribe会在Subscriber进行订阅和反订阅的时候触发回调。当一个Observable通过OnError或者OnCompleted结束的时候，会反订阅所有的Subscriber。在Android中和生命周期绑定起来，因为有些Observable执行不完。
//        doOnSubscribeOn：在开始注册前做一些工作。处于当前线程，而不是subscribeOn指定的线程。
//        DoOnError：在OnError发生的时候触发回调，并将Throwable对象作为参数传进回调函数里。
//        DoOnComplete：会在OnCompleted发生的时候触发回调。
//        DoOnTerminate：会在Observable结束前触发回调，无论是正常还是异常终止。
//        finallyDo、doAfterTerminate：会在Observable结束后触发回调，无论是正常还是异常终止。
//        Materialize、dematerialize：Meterialize操作符将OnNext/OnError/OnComplete都转化为一个Notification对象并按照原来的顺序发射出来,dematerialize相反使用integerNotification.getValue() +”, ” +integerNotification.getKind()可以看到打印值和类型。
//        ObserveOn：观察者工作线程。
//        SubscribeOn：Observable执行线程。
//        Serialize：强制Observable按次序发射数据并且功能是有效的。
//        Subscribe：收到Observable发射的数据和通知后执行的操作，即观察患者。
//        TimeInterval：将一个Observable转换为间隔发射数据的Observable。TimeInterval会拦截发射出来的数据，取代为前后两个发射两个数据的间隔时间。对于第一个发射的数据，其时间间隔为订阅后到首次发射的间隔。
//        Timeout：添加超时机制，如果过了指定的一段时间没有发射数据，就发射一个错误通知。Timeout操作符给Observable加上超时时间，每发射一个数据后就重置计时器，当超过预定的时间还没有发射下一个数据，就抛出一个超时的异常。Rxjava将Timeout实现为很多不同功能的操作符，比如说超时后用一个备用的Observable继续发射数据等。
//        Timestamp：给Observable发射的每个数据项添加一个时间戳。TimeStamp会将每个数据项给重新包装一下，加上了一个时间戳来标明每次发射的时间。
//        Using：创建一个只在Observable的生命周期内存在的一次性资源。Using操作符创建一个在Observable生命周期内存活的资源，也可以这样理解：我们创建一个资源并使用它，用一个Observable来限制这个资源的使用时间，当这个Observable终止的时候，这个资源就会被销毁。sing需要使用三个参数，分别是： 1)创建这个一次性资源的函数；2)创建Observable的函数；3)释放资源的函数。
//
//
//        七、条件和布尔操作
//         这些操作符可用于单个或多个数据项，也可用于Observable
//
//        All：判断Observable发射的所有的数据项是否都满足某个条件。All操作符根据一个函数对源Observable发射的所有数据进行判断，最终返回的结果就是这个判断结果。对发射的所有数据应用这个函数,如果全部都满足则返回true，否则就返回false。
//        Amb：Amb操作符可以将至多9个Observable结合起来，让他们竞争。哪个Observable首先发射了数据（包括onError和onComplete)就会继续发射这个Observable的数据，其他的Observable所发射的数据都会别丢弃。
//        Contains：判断Observable是否会发射一个指定的数据项。Contains操作符用来判断源Observable所发射的数据是否包含某一个数据，如果包含会返回true，如果源Observable已经结束了却还没有发射这个数据则返回false。
//        IsEmpty：IsEmpty操作符用来判断源Observable是否发射过数据，没有发射过数据返回true。Null也是一个数据。
//        DefaultIfEmpty：发射来自原始Observable的数据，如果原始Observable没有发射数据，就发射一个默认数据。
//        SequenceEqual：判断两个Observable是否按相同的数据序列。SequenceEqual操作符用来判断两个Observable发射的数据序列是否相同（发射的数据相同，数据的序列相同，结束的状态相同），如果相同返回true，否则返回false。
//        SkipUntil：SkipUnitl是根据一个标志Observable来判断的，当这个标志Observable没有发射数据的时候，所有源Observable发射的数据都会被跳过；当标志Observable发射了一个数据，则开始正常地发射数据。一直等到skipUntil发射了数据才能发射源Observable的数据，并忽略了此段时间内的数据。
//        SkipWhile：SkipWhile则是根据一个函数来判断是否跳过数据，当函数返回值为true的时候则一直跳过源Observable发射的数据；当函数返回false的时候则开始正常发射数据。
//        TakeUntil：和SkipUtil恰好相反，只获取takeUntil里的Observable之前的数据。
//        TakeWhile：和SkipWhile相反，获取满足skipWhile的数据。
//
//
//        八、算术和聚合操作
//        Concat：将多个Observable结合成一个Observable并发射数据，并且严格按照先后顺序发射数据，前一个Observable的数据没有发射完，不发射后面Observable的数据。
//        Count：Count操作符用来统计源Observable发射了多少个数据，最后将数目给发射出来；如果源Observable发射错误，则会将错误直接报出来；在源Observable没有终止前，count是不会发射统计数据的。
//        Reduce：Reduce操作符应用一个函数接收Observable发射的数据和函数的计算结果作为下次计算的参数，输出最后的结果。跟前面我们了解过的scan操作符很类似，只是scan会输出每次计算的结果，而reduce只会输出最后的结果。
//        Collect：collect用来将源Observable发射的数据给收集到一个数据结构里面，需要使用两个参数：一个产生收集数据结构的函数；一个接收第一个函数产生的数据结构和源Observable发射的数据作为参数的函数。
//
//
//        九、连接操作
//         一些有精确可控的订阅行为的特殊Observable。什么是Connectable Observable：就是一种特殊的Observable对象，并不是Subscrib的时候就发射数据，而是只有对其应用connect操作符的时候才开始发射数据，所以可以用来更灵活的控制数据发射的时机。使用Publish操作符将Observable转换为Connectable Observable，然后可以通过connect控制何时发射。
//        Publish：将一个普通的Observable转换为可连接的。Publish操作符就是用来将一个普通的Observable对象转化为一个Connectable Observable。需要注意的是如果发射数据已经开始了再进行订阅只能接收以后发射的数据。
//        Connect：Connect操作符就是用来触发Connectable Observable发射数据的。应用Connect操作符后会返回一个Subscription对象，通过这个Subscription对象，我们可以调用其unsubscribe方法来终止数据的发射。另外，如果还没有订阅者订阅的时候就应用Connect操作符也是可以使其开始发射数据的。
//        RefCount：RefCount操作符就是将一个Connectable Observable对象再重新转化为一个普通的Observable对象，这时候订阅者进行订阅时就会触发数据的发射。
//        Replay：Replay操作符返回一个Connectable Observable对象并且可以缓存其发射过的数据，这样即使有订阅者在其发射数据之后进行订阅也能收到其之前发射过的数据。不过使用Replay操作符我们最好还是限定其缓存的大小，否则缓存的数据太多了可会占用很大的一块内存。对缓存的控制可以从空间和时间两个方面来实现。直接返回一个connectable observable，不用publish。
//
//
//        十、转换操作
//        to：将Observable转换为另一个对象或数据结构
//
//
//        十一、背压操作符
//        Backpressure：处理Flowable的策略，通常使用场景：这些被观察者Flowable发射的数据比观察者处理的数据要快。
//
//        作者：凌云飞鱼
//        链接：https://www.jianshu.com/p/57af97b8fcce
//        来源：简书
//        著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。