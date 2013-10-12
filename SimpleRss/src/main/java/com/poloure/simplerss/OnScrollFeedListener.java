package com.poloure.simplerss;
import android.view.View;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

class OnScrollFeedListener implements AbsListView.OnScrollListener
{
   private final BaseAdapter m_navigationAdapter;
   private final BaseAdapter m_feedAdapter;

   OnScrollFeedListener(BaseAdapter navigationAdapter, BaseAdapter feedAdapter)
   {
      m_navigationAdapter = navigationAdapter;
      m_feedAdapter = feedAdapter;
   }

   @Override
   public
   void onScrollStateChanged(AbsListView view, int scrollState)
   {
      if(16 == view.getChildAt(0).getTop() &&
            View.VISIBLE == view.getVisibility() && ((AdapterTag) m_feedAdapter).isScreenTouched())
      {
         String url = ((FeedItem) m_feedAdapter.getItem(0)).url;
         AdapterTag.s_readLinks.add(url);
      }

      if(AbsListView.OnScrollListener.SCROLL_STATE_IDLE == scrollState)
      {
         Update.navigation(m_navigationAdapter);
      }
   }

   @Override
   public
   void onScroll(AbsListView v, int fir, int visible, int total)
   {
   }
}
