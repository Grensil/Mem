package sample.practice.memo

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
@SuppressLint("StaticFieldLeak")
class MainActivity : AppCompatActivity(),OnDeleteListener {

    lateinit var db : MemoDatabase

    var memoList : List<MemoEntity> = listOf<MemoEntity>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = MemoDatabase.getInstance(this)!!
        button_add.setOnClickListener{
            val memo = MemoEntity(null,edittext_memo.text.toString())
            edittext_memo.setText("")
            closeKeyboard()
            insertMemo(memo)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        closeKeyboard()
        getAllMemos()


    }

    fun insertMemo(memo: MemoEntity) {
        val insertTask = @SuppressLint("StaticFieldLeak")
        object : AsyncTask<Unit,Unit,Unit>() {

            override fun doInBackground(vararg p0: Unit?) {
                db.memoDAO().insert(memo)

            }


            @SuppressLint("StaticFieldLeak")
            override fun onPostExecute(result: Unit?) {
                super.onPostExecute(result)
                getAllMemos()
            }


        }
        insertTask.execute()
    }
    fun getAllMemos() {
        val getTask = (object : AsyncTask<Unit,Unit,Unit>(){
            override fun doInBackground(vararg p0: Unit?) {
                memoList = db.memoDAO().getAll()
            }

            override fun onPostExecute(result: Unit?) {
                super.onPostExecute(result)
                setRecyclerView(memoList)
            }
        }).execute()

    }
    fun deleteMemo(memo: MemoEntity) {

        val deleteTask = object : AsyncTask<Unit,Unit,Unit>(){
            override fun doInBackground(vararg p0: Unit?) {
                db.memoDAO().delete(memo)
            }

            override fun onPostExecute(result: Unit?) {
                super.onPostExecute(result)
                getAllMemos()
            }
        }
        deleteTask.execute()

    }
    fun setRecyclerView(memoList: List<MemoEntity >) {
        recyclerView.adapter = MyAdapter(this,memoList,this)

    }

    override fun onDeleteListener(memo: MemoEntity) {
        deleteMemo(memo)
    }


    fun closeKeyboard() {
        var view = this.currentFocus

        if(view != null)
        {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}