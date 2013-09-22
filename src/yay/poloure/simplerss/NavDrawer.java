package yay.poloure.simplerss;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

class NavDrawer
{
   static ListView     s_navList;
   static String       s_currentTitle;
   static DrawerLayout s_drawerLayout;

   static final String[] NAV_TITLES = Util.getArray(R.array.nav_titles);
   static final String   NAVIGATION = Util.getString(R.string.navigation_title);

   static ActionBarDrawerToggle DRAWER_TOGGLE;

   NavDrawer(ListView navList, DrawerLayout drawerLayout)
   {
      /* Set the listeners (and save the navigation list to the public static variable). */
      (s_drawerLayout = drawerLayout).setDrawerListener(DRAWER_TOGGLE);
      (s_navList = navList).setOnItemClickListener(new NavDrawerItemClick());

      (DRAWER_TOGGLE = new DrawerToggleClick()).syncState();

      s_navList.setAdapter(new AdapterNavDrawer());
   }

   static
   class RefreshNavAdapter extends AsyncTask<int[], Void, int[]>
   {
      static
      void setTitles(String[] titles)
      {
         AdapterNavDrawer.s_menuArray = titles;
      }

      static
      void setCounts(int[] counts)
      {
         AdapterNavDrawer.s_unreadArray = counts;
      }

      static
      AdapterNavDrawer getAdapter()
      {
         return (AdapterNavDrawer) s_navList.getAdapter();
      }

      @Override
      protected
      int[] doInBackground(int[]... counts)
      {
         /* If null was passed into the task, count the unread items. */
         return 0 == counts[0].length ? Util.getUnreadCounts(FeedsActivity.ctags) : counts[0];
      }

      @Override
      protected
      void onPostExecute(int[] pop)
      {
         /* Set the titles & counts arrays in this file and notifiy the adapter. */
         setTitles(FeedsActivity.ctags);
         setCounts(pop);
         getAdapter().notifyDataSetChanged();
      }
   }

   static
   class DrawerToggleClick extends ActionBarDrawerToggle
   {
      DrawerToggleClick()
      {
         super((Activity) Util.getContext(), s_drawerLayout, R.drawable.ic_drawer,
               R.string.drawer_open, R.string.drawer_close);
      }

      @Override
      public
      void onDrawerClosed(View drawerView)
      {
           /* If the m_title is still "Navigation", then change it back to s_currentTitle. */
         if(FeedsActivity.bar.getTitle().equals(NAVIGATION))
         {
            FeedsActivity.bar.setTitle(s_currentTitle);
         }
      }

      @Override
      public
      void onDrawerOpened(View drawerView)
      {
           /* Save the action bar's m_title to current m_title. Then change the m_title to
           NAVIGATION. */
         s_currentTitle = (String) FeedsActivity.bar.getTitle();
         FeedsActivity.bar.setTitle(NAVIGATION);
      }
   }

   static
   class NavDrawerItemClick implements AdapterView.OnItemClickListener
   {
      static
      void showFragment(Fragment fragment)
      {
         FragmentTransaction tran = FeedsActivity.fman.beginTransaction();
         for(Fragment frag : FeedsActivity.main_fragments)
         {
            if(!frag.isHidden())
            {
               tran.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                     .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                           android.R.anim.fade_in, android.R.anim.fade_out)
                     .hide(frag)
                     .show(fragment)
                     .commit();
               return;
            }
         }
         tran.show(fragment).commit();
      }

      @Override
      public
      void onItemClick(AdapterView parent, View view, int position, long id)
      {
         /* Close the drawer on any click of a navigation item. */
         s_drawerLayout.closeDrawer(s_navList);

         /* Determine the new m_title based on the position of the item clicked. */
         String selectedTitle = 3 < position ? NAV_TITLES[0] : NAV_TITLES[position];

         /* If the item selected was a m_imageViewTag, change the viewpager to that
         image. */
         if(3 < position)
         {
            FeedsActivity.viewpager.setCurrentItem(position - 4);
         }

         /* If the selected title is the title of the current page, exit.
          * This stops the animation from showing on page change.*/

         /* s_currentTitle causes crash because the setTitle functions are not working. */
         /*if(s_currentTitle.equals(selectedTitle))
         {
            return;
         }*/

         /* Hide the current fragment and display the selected one. */
         showFragment(FeedsActivity.main_fragments[3 < position ? 0 : position]);

         /* Set the m_title text of the actionbar to the selected item. */
         FeedsActivity.bar.setTitle(selectedTitle);
      }
   }
}