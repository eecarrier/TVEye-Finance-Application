package edu.gvsu.tveye.adapter;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import edu.gvsu.tveye.fragment.NewsTileFragment;

public class NewsGridAdapter extends FragmentStatePagerAdapter {
	
	private JSONObject response;
	private List<GridPage> pages = new ArrayList<GridPage>();

	public NewsGridAdapter(FragmentManager fm, JSONObject response) {
		super(fm);
		setData(response);
	}
	
	public NewsGridAdapter(FragmentManager fm, List<GridPage> pages) {
		super(fm);
		setPages(pages);
	}
	
	public void setData(JSONObject object) {
		response = object;
		try {
			configurePages();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		notifyDataSetChanged();
	}
	
	public void setPages(List<GridPage> pages) {
		this.pages.clear();
		this.pages.addAll(pages);
		notifyDataSetChanged();
	}
	
	public JSONObject getData() {
		return response;
	}
	
	public void configurePages() throws JSONException {
		pages.clear();
		
		// Calculate the mean value for interestLevel
		double mean = 0d;
		int count = 0;
		JSONArray list = response.getJSONArray("list");
		List<JSONObject> stories = new ArrayList<JSONObject>();
		for(int i = 0; i < list.length(); i++) {
			JSONObject story = list.getJSONObject(i);
			double interestLevel = story.optDouble("interestLevel");
			if(interestLevel != Double.NaN) {
				mean += interestLevel;
				count++;
				stories.add(story);
			}
		}
		mean /= count;
		
		// The desired interest level per page is the mean * average number of articles
		GridPage currentPage = new GridPage(mean, getGoalLevelModifier(stories.size(), count));
		while(!stories.isEmpty()) {
			// Try to add a story to a page, if rejected and it's the last one force it
			int location = (int) (Math.random() * stories.size());
			JSONObject story = stories.get(location);
			if(currentPage.add(story)) {
				// Story added to the current page, remove it from our list
				stories.remove(location);
			}
			if(currentPage.isFull() || stories.isEmpty()) {
				pages.add(currentPage);
				currentPage = new GridPage(mean, getGoalLevelModifier(stories.size(), count));
			}
			Log.d("NewsGridAdapter", "Remaining: " + stories.size());
		}
		Log.d("NewsGridAdapter", "Created " + pages.size() + " pages");
	}
	
	// The modifier, return 10 if we are running out of items, 5 if we have everything
	// The reason we want a low modifier if we have all items is because we want interesting
	// articles on the front page
	private double getGoalLevelModifier(int remaining, int total) {
		double difference = total - remaining;
		return 5d + (difference / (double) total) * 5d;
	}
	
	public List<GridPage> getPages() {
		return pages;
	}
	
	public int getCount() {
		return pages.size();
	}

	@Override
	public Fragment getItem(int position) {
		return NewsTileFragment.newInstance(pages.get(position).toJSONArray(), position);
	}
	
	/**
	 * A grid page contains 3-6 articles.
	 * @author gregzavitz
	 */
	public class GridPage extends ArrayList<JSONObject> {	
		
		double currentLevel = 0, goalLevel;
		int goalCount, fails = 0;
		
		public GridPage(double mean, double modifier) {
			goalLevel = mean * modifier;
			goalCount = (int) modifier;
		}
		
		public boolean isFull() {
			return size() == goalCount;
		}
		
		public boolean add(JSONObject story) {
			double tempLevel = currentLevel + story.optDouble("interestLevel", 0);
			double expectedLevel = goalLevel * size();
			// If the suggested story is at least 70% good enough for this page, accept
			if(tempLevel / expectedLevel > 0.7 || ++fails == 5) {
				currentLevel = tempLevel;
				fails = 0;
				return super.add(story);
			} else {
				return false;
			}
		}
		
		public JSONArray toJSONArray() {
			JSONArray array = new JSONArray();
			for(JSONObject story : this) {
				array.put(story);
			}
			return array;
		}
		
	}
	
}
