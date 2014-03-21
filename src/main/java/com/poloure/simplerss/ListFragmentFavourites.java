/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.poloure.simplerss;

import android.app.ListFragment;
import android.os.Bundle;
import android.widget.AbsListView;
import android.widget.ListView;

public
class ListFragmentFavourites extends ListFragment
{
   @Override
   public
   void onActivityCreated(Bundle savedInstanceState)
   {
      super.onActivityCreated(savedInstanceState);

      FeedsActivity activity = (FeedsActivity) getActivity();
      ListView listView = getListView();

      setListAdapter(new AdapterFavourites(activity));

      registerForContextMenu(listView);
      listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
      listView.setOnItemClickListener(new OnItemClickWebView());
      listView.setMultiChoiceModeListener(new MultiModeListener(listView, activity));
   }
}