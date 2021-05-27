package com.sedavnyh.todo.fragments.list

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.*
import com.google.android.material.snackbar.Snackbar
import com.sedavnyh.todo.R
import com.sedavnyh.todo.data.models.ToDoData
import com.sedavnyh.todo.data.viewmodel.ToDoViewModel
import com.sedavnyh.todo.databinding.FragmentListBinding
import com.sedavnyh.todo.fragments.SharedViewModel
import com.sedavnyh.todo.fragments.list.adapter.ListAdapter
import com.sedavnyh.todo.utils.hideKeyboard
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator


class ListFragment : Fragment(), SearchView.OnQueryTextListener {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    private val adapter: ListAdapter by lazy { ListAdapter() }
    private val mToDoViewModel: ToDoViewModel by viewModels()
    private val mSharedViewModel: SharedViewModel by viewModels()
    private var typeLayout:String? = "linear_layout"

    // Start view, setup binding, recycler
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //Data binding
        _binding = FragmentListBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.mSharedViewModel = mSharedViewModel

        //Load global setting
        loadSettings()

        //Setup recycler view
        setupRecyclerView()

        // Observe changes to data
        mToDoViewModel.getAllData.observe(viewLifecycleOwner, {
            mSharedViewModel.checkIfDatabaseIsEmpty(it)
            adapter.setData(it)
        })
        mSharedViewModel.emptyDatabase.observe(viewLifecycleOwner, {
            showEmptyDatabaseViews(it)
        })

        //set menu
        setHasOptionsMenu(true)

        //Hide soft keyboard
        hideKeyboard(requireActivity())

        return binding.root
    }

    private fun loadSettings() {
        val sharedPreference = PreferenceManager.getDefaultSharedPreferences(context)
        typeLayout = sharedPreference.getString("type_layout", "liner_layout")
    }

    private fun showEmptyDatabaseViews(emptyDatabase: Boolean) {
        if(emptyDatabase){
            binding.noDataTextView.visibility = View.VISIBLE
            binding.noDataImageView.visibility = View.VISIBLE
        } else {
            binding.noDataTextView.visibility = View.INVISIBLE
            binding.noDataImageView.visibility = View.INVISIBLE
        }
    }

    private fun setupRecyclerView() {
        val recyclerView = binding.recyclerView
        recyclerView.adapter = adapter

        if (typeLayout == "linear_layout")
            recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        else
            recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        recyclerView.itemAnimator = SlideInUpAnimator().apply {
            addDuration = 300
        }

        swipeToDelete(recyclerView)
    }

    private fun swipeToDelete(recyclerView: RecyclerView) {
        val swipeToDeleteCallback = object : SwipeToDelete() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val itemToDelete = adapter.dataList[viewHolder.adapterPosition]
                // delete item
                mToDoViewModel.deleteData(itemToDelete)
                adapter.notifyItemRemoved(viewHolder.adapterPosition)

                //call restore
                restoreDeletedData(viewHolder.itemView, itemToDelete)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun restoreDeletedData(view: View, deletedItem: ToDoData ) {
        val snackBar = Snackbar.make(
            view, "Deleted '${deletedItem.title}'", Snackbar.LENGTH_LONG
        )
        snackBar.setAction("Undo") {
            mToDoViewModel.insertData(deletedItem)
        }
        snackBar.show()
    }

    // Delete data from menu button
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_delete_all -> confirmDeleteAll()
            R.id.menu_priority_high -> mToDoViewModel.sortByHighPriority.observe(
                viewLifecycleOwner,
                Observer {
                    adapter.setData(it)
                })
            R.id.menu_priority_low -> mToDoViewModel.sortByLowPriority.observe(
                viewLifecycleOwner,
                Observer {
                    adapter.setData(it)
                })
            R.id.menu_date_create -> mToDoViewModel.getAllData.observe(this, Observer { data ->
                adapter.setData(data)
            })
            R.id.menu_settings -> findNavController().navigate(R.id.action_listFragment_to_settingsFragment)
        }
        return super.onOptionsItemSelected(item)
    }

    // Add menu to navbar
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_fragment_menu, menu)

        // enable searching
        val search = menu.findItem(R.id.menu_search)
        val searchView = search.actionView as? SearchView
        searchView?.isSubmitButtonEnabled = true
        searchView?.setOnQueryTextListener(this)
    }

    // Confirmation alert on deletion
    private fun confirmDeleteAll() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes") { _, _ ->
            mToDoViewModel.deleteAllData()
            Toast.makeText(requireContext(), "All items successfully removed", Toast.LENGTH_SHORT)
                .show()
        }
        builder.setNegativeButton("No") { _, _ -> }
        builder.setTitle("Delete all items?")
        builder.setMessage("Are you sure you want to remove all items?")
        builder.create().show()
    }

    //Destroy view
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //Query for searching
    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            searchThroughDatabase(query)
        }
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if (query != null) {
            searchThroughDatabase(query)
        }
        return true
    }

    private fun searchThroughDatabase(query: String) {
        var searchQuery = "%$query%"
        mToDoViewModel.searchDatabase(searchQuery).observe(this, Observer { list ->
            list?.let {
                adapter.setData(it)
            }
        })
    }
}