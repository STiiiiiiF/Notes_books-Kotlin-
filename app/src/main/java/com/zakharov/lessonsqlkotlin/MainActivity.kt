package com.zakharov.lessonsqlkotlin

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zakharov.lessonsqlkotlin.databinding.ActivityMainBinding
import com.zakharov.lessonsqlkotlin.db.MyAdapter
import com.zakharov.lessonsqlkotlin.db.MyDbManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainActivity() : AppCompatActivity() {

    private val myDbManager = MyDbManager(this)
    private var myAdapter = MyAdapter(ArrayList(), this)
    private var job: Job? = null


    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        initSearchView()
    }

    override fun onResume() = with(binding) {
        super.onResume()
        myDbManager.openDb()
        fillAdapter("")

    }

    fun onClickNew(view: View) = with(binding) {
        val intent = Intent(this@MainActivity, EditActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        myDbManager.closeDb()
    }

    private fun init() = with(binding) {
        rcView.layoutManager = LinearLayoutManager(this@MainActivity)
        val swapHelper = getSwapManager()
        swapHelper.attachToRecyclerView(rcView)
        rcView.adapter = myAdapter
    }

    private fun initSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                fillAdapter(newText!!)
                return true
            }
        })
    }

    private fun fillAdapter(text: String) {
        job?.cancel()
        job = CoroutineScope(Dispatchers.Main).launch {
            val list = myDbManager.readDbData(text)
            myAdapter.updateAdapter(list)
            if (list.size > 0) {
                binding.tvNoElements.visibility = View.GONE
            } else {
                binding.tvNoElements.visibility = View.VISIBLE
            }
        }

    }

    private fun getSwapManager(): ItemTouchHelper {

        return ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                myAdapter.removeItem(viewHolder.adapterPosition, myDbManager)

            }

        })
    }

}