package yay.poloure.simplerss;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Set;

final
class Util
{
   static       String     s_storage                 = "";
   static final int[]      EMPTY_INT_ARRAY           = new int[0];
   static final String[]   EMPTY_STRING_ARRAY        = new String[0];
   static final String[][] EMPTY_STRING_STRING_ARRAY = new String[0][0];

   static final String[] MEDIA_ERROR_MESSAGES = getArray(R.array.media_errors);

   static final String[] MEDIA_ERRORS = {
         Environment.MEDIA_UNMOUNTED,
         Environment.MEDIA_UNMOUNTABLE,
         Environment.MEDIA_SHARED,
         Environment.MEDIA_REMOVED,
         Environment.MEDIA_NOFS,
         Environment.MEDIA_MOUNTED_READ_ONLY,
         Environment.MEDIA_CHECKING,
         Environment.MEDIA_BAD_REMOVAL
   };

   private
   Util()
   {
   }

   /* index throws an ArrayOutOfBoundsException if not handled. */
   static
   <T> int index(T[] array, T value)
   {
      if(null == array)
      {
         return -1;
      }
      for(int i = 0; i < array.length; i++)
      {
         if(array[i].equals(value))
         {
            return i;
         }
      }
      return -1;
   }

   static
   String[] arrayRemove(String[] a, int index)
   {
      if(null == a || 0 == a.length)
      {
         return Util.EMPTY_STRING_ARRAY;
      }

      String[] b = new String[a.length - 1];
      System.arraycopy(a, 0, b, 0, index);
      if(a.length != index)
      {
         System.arraycopy(a, index + 1, b, index, a.length - index - 1);
      }
      return b;
   }

   static
   void updateTags()
   {
      /* Since this function is static, we can not rely on the fields being
       * non-null. */

      /* Update ctags. */
      FeedsActivity.ctags = Read.file(FeedsActivity.GROUP_LIST);

      /* If no tags exists, add the all m_imageViewTag. */
      if(0 == FeedsActivity.ctags.length)
      {
         Write.single(FeedsActivity.GROUP_LIST, FeedsActivity.all + FeedsActivity.NL);
         FeedsActivity.ctags = new String[]{FeedsActivity.all};
      }

      if(null != FeedsActivity.viewpager)
      {
         FeedsActivity.viewpager.getAdapter().notifyDataSetChanged();
         /* Does not run on first Update. */
         Update.navigation();
      }
   }

   static
   int gotoLatestUnread(Datum[] items, boolean update, int page)
   {

      if(null == FeedsActivity.viewpager)
      {
         return -1;
      }

      if(update)
      {
         page = FeedsActivity.viewpager.getCurrentItem();
      }

      if(null == items)
      {
         AdapterCard adp = getCardAdapter(page);
         if(null == adp)
         {
            return -1;
         }

         items = adp.m_items;
      }

      int i;
      for(i = items.length - 1; 0 <= i; i--)
      {
         int pos = items.length - i - 1;
         if(!SetHolder.read_items.contains(items[pos].url))
         {
            break;
         }
      }

      /* 0 is the top. links.length - 1 is the bottom.
       * May not be true anymore.*/
      if(update)
      {
         ListView lv = getListView(page);
         if(null == lv)
         {
            return -1;
         }

         lv.setSelection(i);
      }
      return i;
   }

   static
   boolean isUnmounted()
   {
      /* TODO If setting to force sd is true, return false. */
      /*if(!internal.equals(s_storage))
         return false;*/

      /* Check to see if we can Write to the media. */
      String mounted = Environment.MEDIA_MOUNTED;
      if(!mounted.equals(Environment.getExternalStorageState()))
      {
         post(Write.MEDIA_UNMOUNTED);
         return true;
      }

      return false;
   }

   /* Replaces all '/'s with '-' to emulate a folder directory layout in
    * data/data. */
   static
   String getInternalName(String path)
   {
      String substring = path.substring(
            path.indexOf(FeedsActivity.SEPAR + "files" + FeedsActivity.SEPAR) + 7);
      return substring.replaceAll(FeedsActivity.SEPAR, "-");
   }

   /* For these two functions, check for null. Should only really be
    * null if called from the ServiceUpdate. */
   static
   AdapterCard getCardAdapter(int page)
   {
      ListView list = getListView(page);
      if(null == list)
      {
         return null;
      }

      return (AdapterCard) list.getAdapter();
   }

   /* This is the second one. */
   private static
   ListView getListView(int page)
   {
      FragmentManager fman = FeedsActivity.fman;
      ViewPager viewpager = FeedsActivity.viewpager;
      if(null == fman || null == viewpager)
      {
         return null;
      }

      String tag = "android:switcher:" + viewpager.getId() + ':' + page;
      return ((ListFragment) fman.findFragmentByTag(tag)).getListView();
   }

   /* For feed files. */
   static
   String getPath(String feed, String append)
   {
      String feed_folder = feed + FeedsActivity.SEPAR;

      if("images".equals(append))
      {
         return feed_folder + FeedsActivity.IMAGE_DIR;
      }
      if("thumbnails".equals(append))
      {
         return feed_folder + FeedsActivity.THUMBNAIL_DIR;
      }

      return feed_folder + append;
   }

   /* This should never return null and so do not check. */
   static
   Context getContext()
   {
      /* If running get the context from the activity, else ask the service. */
      if(null != FeedsActivity.con)
      {
         return FeedsActivity.con;
      }

      else
      {
         return null != ServiceUpdate.s_serviceContext ? ServiceUpdate.s_serviceContext : null;
      }
   }

   static
   LayoutInflater getLayoutInflater()
   {
      String inflate = Context.LAYOUT_INFLATER_SERVICE;
      Context con = getContext();
      return (LayoutInflater) con.getSystemService(inflate);
   }

   /* Safe to call at anytime. */
   static
   int getScreenWidth()
   {
      return getContext().getResources().getDisplayMetrics().widthPixels;
   }

   /* This function will return null if it fails. Check for null each time.
    * It should be pretty safe and efficient to call all the time. */
   static
   String getStorage()
   {
      if(!s_storage.isEmpty())
      {
         return s_storage;
      }
      /* Check the media state for any undesirable states. */
      /* TODO Check to see if it is the desired first then skip these. */
      String state = Environment.getExternalStorageState();
      for(int i = 0; i < MEDIA_ERRORS.length; i++)
      {
         if(state.equals(MEDIA_ERRORS[i]))
         {
            post(MEDIA_ERROR_MESSAGES[i]);
            return null;
         }
      }

      /* If it has reached here the state is MEDIA_MOUNTED and we can continue.
         Build the s_storage string depending on android version. */
      Context context = getContext();
      String sep = FeedsActivity.SEPAR;

      if(FeedsActivity.FROYO)
      {
         s_storage = context.getExternalFilesDir(null).getAbsolutePath() + sep;
      }
      else
      {
         String name = context.getPackageName();
         File ext = Environment.getExternalStorageDirectory();
         s_storage = ext.getAbsolutePath() + sep + "Android" + sep + "data" + sep + name + sep +
               "files" + sep;

         /* If the folder does not exist, create it. */
         File storage_file = new File(s_storage);
         if(!storage_file.exists())
         {
            storage_file.mkdirs();
         }
      }
      return s_storage;
   }

   static
   class SetHolder
   {
      static final Set<String> read_items = Read.set(FeedsActivity.READ_ITEMS);
   }

   static
   int[] getUnreadCounts(String... ctags)
   {
      int size = ctags.length;
      int[] unread_counts = new int[size];

      /* read_items == null when called from the service for notifications. */

      int total = 0;
      for(int i = 1; i < size; i++)
      {
         int unread = 0;
         String[] urls = Read.file(getPath(ctags[i], FeedsActivity.URL));
         for(String url : urls)
         {
            if(!SetHolder.read_items.contains(url))
            {
               unread++;
            }
         }

         total += unread;
         unread_counts[i] = unread;
      }

      unread_counts[0] = total;
      return unread_counts;
   }

   static
   void setStripColor(PagerTabStrip strip)
   {
      String colorSettingsPath = FeedsActivity.SETTINGS + FeedsActivity.STRIP_COLOR;

      /* Read the colour from the settings/colour file, if blank, use blue. */
      String[] check = Read.file(colorSettingsPath);
      String color = 0 == check.length ? "blue" : check[0];

      /* Find the colour stored in adapter_stettings_interface that we want. */
      int pos = index(AdapterSettingsUi.HOLO_COLORS, color);
      if(-1 != pos)
      {
         strip.setTabIndicatorColor(AdapterSettingsUi.COLOR_INTS[pos]);
      }
   }

   static
   Intent getServiceIntent(int page)
   {
      /* Load notification boolean. */
      String path = FeedsActivity.SETTINGS + AdapterSettingsFunctions.FILE_NAMES[3] +
            FeedsActivity.TXT;
      String[] check = Read.file(path);
      Context con = getContext();

      boolean notif = 0 != check.length && strbol(check[0]);
      Intent intent = new Intent(con, ServiceUpdate.class);
      intent.putExtra("GROUP_NUMBER", page);
      intent.putExtra("NOTIFICATIONS", notif);
      return intent;
   }

   static
   void setCardAlpha(TextView title, TextView url, ImageView image, TextView des, String link)
   {
      if(!FeedsActivity.HONEYCOMB)
      {
         return;
      }

      Resources res = getContext().getResources();

      if(SetHolder.read_items.contains(link))
      {
         title.setTextColor(res.getColor(R.color.title_grey));
         url.setTextColor(res.getColor(R.color.link_grey));

         if(null != des)
         {
            des.setTextColor(res.getColor(R.color.des_grey));
         }

         if(null != image)
         {
            image.setAlpha(0.5f);
         }
      }
      else
      {
         title.setTextColor(res.getColor(R.color.title_black));
         url.setTextColor(res.getColor(R.color.link_black));

         if(null != des)
         {
            des.setTextColor(res.getColor(R.color.des_black));
         }

         if(null != image)
         {
            image.setAlpha(1.0f);
         }
      }
   }

   /* Changes the ManageRefresh menu item to an animation if mode = true. */
   static
   void setRefreshingIcon(boolean mode)
   {
      if(null == FeedsActivity.optionsMenu)
      {
         return;
      }

      /* Find the menu item by ID caled ManageRefresh. */
      MenuItem item = FeedsActivity.optionsMenu.findItem(R.id.refresh);
      if(null == item)
      {
         return;
      }

      /* Change it depending on the mode. */
      if(mode)
      {
         MenuItemCompat.setActionView(item, R.layout.progress_circle);
      }
      else
      {
         MenuItemCompat.setActionView(item, null);
      }
   }

   /* Use this after content has been updated and you need to ManageRefresh */
   static
   void refreshPages(int page)
   {
      Update.page(0);
      if(0 == page)
      {
         for(int i = 1; i < FeedsActivity.ctags.length; i++)
         {
            Update.page(i);
         }
      }
      else
      {
         Update.page(page);
      }
   }

   /* This will log and toast any message. */
   static
   void post(CharSequence message)
   {
      /* If this is called from off the UI thread, it will die. */
      try
      {
         /* If the activity is running, make a toast notification.
            * Log the event regardless. */
         if(null != FeedsActivity.service_handler)
         {
            Toast.makeText(FeedsActivity.con, message, Toast.LENGTH_LONG).show();
         }

         /* This function can be called when no media, so this is optional. */
         String mounted = Environment.MEDIA_MOUNTED;
         if(mounted.equals(Environment.getExternalStorageState()))
         {
            Write.log((String) message);
         }
      }
      catch(RuntimeException e)
      {
         e.printStackTrace();
      }
   }

   static
   boolean remove(String path)
   {
      path = getStorage() + path;
      getContext().deleteFile(getInternalName(path));
      return new File(path).delete();
   }

   static
   boolean rmdir(File directory)
   {
      if(directory.isDirectory())
      {
         for(String child : directory.list())
         {
            boolean success = !rmdir(new File(directory, child));
            if(success)
            {
               return false;
            }
         }
      }
      return directory.delete();
   }

   static
   void mkdir(String path)
   {
      path = getStorage() + path;
      File folder = new File(path);
      if(!folder.exists())
      {
         folder.mkdirs();
      }
   }

   /* Wrappers for neatness. */

   static
   boolean move(String a, String b)
   {
      a = getStorage() + a;
      b = getStorage() + b;
      return new File(a).renameTo(new File(b));
   }

   static
   boolean isUsingSd()
   {
      /* Return true if force sd setting is true. */
      return true;
   }

   static
   boolean strbol(String str)
   {
      return Boolean.parseBoolean(str);
   }

   static
   int stoi(String str)
   {
      return Integer.parseInt(str);
   }

   static
   boolean exists(String path)
   {
      path = getStorage() + path;
      if(isUsingSd() || path.contains(FeedsActivity.IMAGE_DIR) ||
            path.contains(FeedsActivity.THUMBNAIL_DIR))
      {
         return !new File(path).exists();
      }
      else
      {
         String in_path = getInternalName(path);
         return !getContext().getFileStreamPath(in_path).exists();
      }
   }

   static
   void setText(CharSequence str, View v, int id)
   {
      ((TextView) v.findViewById(id)).setText(str);
   }

   static
   String getText(View v, int id)
   {
      return ((TextView) v.findViewById(id)).getText().toString().trim();
   }

   static
   String getText(TextView v)
   {
      return v.getText().toString().trim();
   }

   /* Returns a zero-length array if the resource is not found and logs the
    * event in log. */
   static
   String[] getArray(int resource)
   {
      String[] array = new String[0];
      Context con = getContext();
      try
      {
         array = con.getResources().getStringArray(resource);
      }
      catch(Resources.NotFoundException e)
      {
         e.printStackTrace();
         Write.log(resource + " does not exist.");
      }
      return array;
   }

   static
   String getString(int resource)
   {
      String str = "";
      Context con = getContext();
      try
      {
         str = con.getString(resource);
      }
      catch(Resources.NotFoundException e)
      {
         e.printStackTrace();
         Write.log(resource + " does not exist.");
      }
      return str;
   }
}