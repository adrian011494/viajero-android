package cu.sitrans.viajero.utils

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.annotations.CheckReturnValue
import io.reactivex.annotations.SchedulerSupport

@CheckReturnValue
@SchedulerSupport(SchedulerSupport.NONE)
fun <U : Any, T : Iterable<U>> Single<T>.flatMapIterable(): Observable<U> {
    return this.flatMapObservable {
        Observable.fromIterable(it)
    }
}

@CheckReturnValue
@SchedulerSupport(SchedulerSupport.NONE)
fun <T : Any, U : Any> Observable<T>.notOfType(clazz: Class<U>): Observable<T> {
    checkNotNull(clazz) { "clazz is null" }
    return filter { !clazz.isInstance(it) }
}


fun String.toDate(): String {

    val result = this.split("T")
    return if (result.isNotEmpty()) {

        val date = result.first().replace("-", "/")

        val time = result[1].split(".").firstOrNull() ?: ""

        "$date $time"
    } else
        ""


}