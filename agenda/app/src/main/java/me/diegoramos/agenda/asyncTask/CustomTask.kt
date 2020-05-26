package me.diegoramos.agenda.asyncTask

import android.os.AsyncTask

class CustomTask(private val delegate: TaskDelegate) : AsyncTask<Void, Void, Void>() {
    override fun doInBackground(vararg params: Void?): Void? {
        delegate.background()
        return null
    }

    override fun onPostExecute(result: Void?) {
        super.onPostExecute(result)
        delegate.onFinish()
    }
}

interface TaskDelegate {
    fun background()
    fun onFinish()
}