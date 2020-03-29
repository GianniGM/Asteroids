package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.database.Asteroid
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import com.udacity.asteroidradar.detail.PictureOfDay
import com.udacity.asteroidradar.utils.CalendarUtils
import com.udacity.asteroidradar.utils.dateIsBetween
import kotlinx.android.synthetic.main.fragment_main.*

/**
 * Fragment that shows all the asteroids list and the daily image
 */
class MainFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        val application = requireNotNull(this.activity).application
        val dataBase = AsteroidsDatabase.getInstance(application).asteroidsDao
        val viewModelFactory = ViewModelFactory(dataBase, application)
        ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
    }

    private var asteroidsAdapter: AsteroidsAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = setBinding(inflater)

        setHasOptionsMenu(true)
        setNavigation()
        onUpdateDailyImage()
        binding.statusLoadingWheel.isVisible = true

        binding.activityMainImageOfTheDay.contentDescription = context?.getString(
            R.string.this_is_nasa_s_picture_of_day_showing_nothing_yet
        )
        onUpdateAsteroidList{
            asteroidsAdapter?.submitList(it)

            // I honestly don't know how to get binding so I used a lambda for the loading
            binding.statusLoadingWheel.isGone = true
        }
        return binding.root
    }

    private fun setBinding(inflater: LayoutInflater): FragmentMainBinding {
        val binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        asteroidsAdapter = AsteroidsAdapter(AsteroidClickListner {
            viewModel.onAsteroidClicked(it)
        })
        binding.asteroidRecycler.adapter = asteroidsAdapter
        return binding
    }

    private fun setNavigation() {
        viewModel.navigateToDetails.observe(viewLifecycleOwner, Observer { asteroid ->
            asteroid?.let {
                this.findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
            }
        })
        viewModel.onAsteroidNavigated()
    }

    private fun onUpdateDailyImage() = viewModel.picOfTheDay.observe(
        viewLifecycleOwner, Observer {
            setUpDailyPic(it)
        }
    )

    private fun onUpdateAsteroidList(doOnUpdate: (List<Asteroid>) -> Unit) {
        viewModel.asteroids.observe(
            viewLifecycleOwner, Observer {
                doOnUpdate(it)
            }
        )
    }

    private fun setUpDailyPic(pictureOfDay: PictureOfDay) {
        Picasso.with(context)
            .load(pictureOfDay.url)
            .placeholder(R.drawable.placeholder_picture_of_day)
            .into(activity_main_image_of_the_day)

        activity_main_image_of_the_day.contentDescription = context?.getString(
            R.string.nasa_picture_of_day_content_description_format,
            pictureOfDay.title
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val calendar = CalendarUtils.getCalendarInstance()
        val today = calendar.today
        val endDate = calendar.setEndDate(Constants.DEFAULT_END_DATE_DAYS).endDate

        return when (item.itemId) {
            R.id.show_all_menu -> {
                filterWeeklyAsteroids(today, endDate)
                true
            }
            R.id.today_asteroids_menu -> {
                filterDailyAsteroids(today)
                true
            }
            else -> {
//                "saved" asteroids are already what we have
//                give we are using a repository
                onUpdateAsteroidList {
                    asteroidsAdapter?.submitList(it)
                }
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun filterDailyAsteroids(today: String) {
        onUpdateAsteroidList {
            val filterList =
                it.asSequence()
                    .filter { asteroid ->
                        asteroid.closeApproachDate == today
                    }.toList()
            asteroidsAdapter?.submitList(filterList)
        }
    }

    private fun filterWeeklyAsteroids(today: String, endDate: String) {
        onUpdateAsteroidList { asteroids ->
            val filteredList = asteroids.asSequence()
                .filter {
                    it.closeApproachDate dateIsBetween (today to endDate)
                }.toList()
            asteroidsAdapter?.submitList(filteredList)
        }
    }

}
