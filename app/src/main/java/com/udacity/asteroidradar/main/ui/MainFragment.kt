package com.udacity.asteroidradar.main.ui

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.api.NetworkUtils
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import com.udacity.asteroidradar.main.MainViewModel
import com.udacity.asteroidradar.main.MainViewModelFactory
import com.udacity.asteroidradar.view.AsteroidAdapter
import com.udacity.asteroidradar.view.AsteroidListener

class MainFragment : Fragment() {

    lateinit var binding: FragmentMainBinding
    private val viewModel: MainViewModel by lazy {
        /** this instance is not being used */
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_main, container, false
        )
        val application = requireNotNull(this.activity).application
        val dataSource = AsteroidDatabase.getDatabaseInstance(application).asteroidDao
        val viewModelFactory = MainViewModelFactory(dataSource, application)
        val mainFragmentViewModel =
            ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

        binding.lifecycleOwner = this
        binding.mainAsteroidXmlViewModel = mainFragmentViewModel
        val adapter = AsteroidAdapter(AsteroidListener { asteroid ->
            run {
                mainFragmentViewModel.onAsteroidClicked(asteroid)
            }
        })
        /**
         * ORIGINAL: get Adapter-handler and assign it to binding-adapter to manager recyclerView
        binding.asteroidRecycler.adapter = adapter
        mainFragmentViewModel.asteroids.observe(viewLifecycleOwner, Observer {
        it.let {
        adapter.submitList(it)
        }
        })*/

        mainFragmentViewModel.status.observe(viewLifecycleOwner, Observer { })
        mainFragmentViewModel.pictureOfDay.observe(viewLifecycleOwner, Observer { })
        /** REPOSITORY: get Adapter-handler and assign it to binding-adapter to manager recyclerView */
        binding.asteroidRecycler.adapter = adapter
        mainFragmentViewModel.dbDataMainViewModel.observe(viewLifecycleOwner, {
            it.let {
                adapter.submitList(it)
            }
        })
        mainFragmentViewModel.navigateToDetailsFragment.observe(
            viewLifecycleOwner,
            Observer { asteroidsToNavigate ->
                asteroidsToNavigate?.let {
                    this.findNavController().navigate(
                        MainFragmentDirections.actionMainFragmentToDetailFragment(
                            asteroidsToNavigate
                        )
                    )
                    mainFragmentViewModel.onAsteroidNavigated()
                }
            })

        /** executes asteroidApi for fetching Asteroid-Properties, should network become available */
        NetworkUtils.isNetworkAvailable.observe(viewLifecycleOwner) {
            if (it) {
                refreshAsteroidDataWhenNetworkIsAvailable(mainFragmentViewModel)
            }
        }

        setHasOptionsMenu(true)
        return binding.root
    }

    private fun refreshAsteroidDataWhenNetworkIsAvailable(mainFragmentViewModel: MainViewModel) {
        mainFragmentViewModel.mainViewModelRefreshAsteroidData()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return true
    }
}