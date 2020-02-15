package com.foreknowledge.photomemo

import android.content.Context
import android.content.Intent
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

        memo_list.setHasFixedSize(true)
        memo_list.layoutManager = LinearLayoutManager(context)

        btn_create_memo.setOnClickListener { switchTo(context, CreateMemoActivity::class.java) }

        AutoPermissions.Companion.loadAllPermissions(this, PERMISSION_REQUEST_CODE)
    }

    override fun onResume() {
        super.onResume()

        memoAdapter = MemoListAdapter(MemoDbTable(this).readAllMemo(), object: ItemClickListener{
            override fun onClick(view: View, position: Int) {
                val bundle = Bundle()
                bundle.putLong("memoId", memoAdapter.getItem(position).id)

                switchTo(context, DetailMemoActivity::class.java, bundle)
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

object RequestCode {
    const val PERMISSION_REQUEST_CODE = 100
    const val CHOOSE_CAMERA_IMAGE = 101
    const val CHOOSE_GALLERY_IMAGE = 102

}

fun switchTo(context: Context, activity: Class<*>, bundle: Bundle? = null) {
    val intent = Intent(context, activity)
    bundle?.let{ intent.putExtras(bundle) }

    context.startActivity(intent)
}
