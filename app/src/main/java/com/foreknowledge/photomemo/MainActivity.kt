package com.foreknowledge.photomemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.foreknowledge.photomemo.RequestCode.PERMISSION_REQUEST_CODE
import com.pedro.library.AutoPermissions
import com.pedro.library.AutoPermissionsListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), AutoPermissionsListener {
    private val context = this@MainActivity
    private val tag = MainActivity::class.java.simpleName

    private lateinit var memoAdapter: MemoListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        memo_list.layoutManager = linearLayoutManager
        memo_list.setHasFixedSize(true)

        btn_create_memo.setOnClickListener { switchTo(context, CreateMemoActivity::class.java) }

        AutoPermissions.Companion.loadAllPermissions(this, PERMISSION_REQUEST_CODE)
    }

    override fun onResume() {
        super.onResume()

        val memos = MemoDbTable(this).readAllMemo()
        if (memos.isEmpty())
            memo_notice.visibility = View.VISIBLE
        else
            memo_notice.visibility = View.INVISIBLE

        memoAdapter = MemoListAdapter(memos, object: ItemClickListener{
            override fun onClick(view: View, position: Int) {
                val extras = Bundle()
                extras.putLong(KeyName.MEMO_ID, memoAdapter.getItem(position).id)

                switchTo(context, DetailMemoActivity::class.java, extras)
            }
        })

        memo_list.adapter = memoAdapter
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        AutoPermissions.Companion.parsePermissions(this, requestCode, permissions, this)
    }

    override fun onDenied(requestCode: Int, permissions: Array<String>) {
        Log.d(tag,"permissions denied : ${permissions.size}")
    }

    override fun onGranted(requestCode: Int, permissions: Array<String>) {
        Log.d(tag,"permissions granted : ${permissions.size}")
    }
}
