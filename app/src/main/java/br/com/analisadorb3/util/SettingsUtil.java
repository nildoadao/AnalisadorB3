package br.com.analisadorb3.util;
import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;


public class SettingsUtil {

    public static final String SAVED_STOCKS = "SavedStocks";
    private Context context;

    public SettingsUtil(Context context){
        this.context = context;
    }

    public List<String> getFavouriteStocks(){
        SharedPreferences settings = context.getSharedPreferences(SAVED_STOCKS, 0);
        try{
            JSONArray json = new JSONArray(settings.getString("favouriteStocks", ""));
            List<String> list = new ArrayList<>();
            for(int i = 0; i < json.length(); i++){
                list.add(json.get(i).toString());
            }
            return list;
        }
        catch (Exception e){
            return new ArrayList<>();
        }
    }

    public boolean saveFavouriteStock(String stock){
        if(stock == null || stock.equals(""))
            return false;

        List<String> favouriteStocks = getFavouriteStocks();

        if(favouriteStocks.contains(stock))
            return false;

        favouriteStocks.add(stock);

        if(favouriteStocks.size() > 5)
            return false;

        String json = JSONObject.wrap(favouriteStocks).toString();
        SharedPreferences settings = context.getSharedPreferences(SAVED_STOCKS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("favouriteStocks", json);
        editor.commit();

        return true;
    }

    public boolean removeFavouriteStock(String stock){
        if(stock == null || stock.equals(""))
            return false;

        List<String> favouriteStocks = getFavouriteStocks();

        if(!favouriteStocks.contains(stock))
            return false;

        favouriteStocks.remove(stock);
        String json = JSONObject.wrap(favouriteStocks).toString();
        SharedPreferences settings = context.getSharedPreferences(SAVED_STOCKS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("favouriteStocks", json);
        editor.commit();

        return true;

    }
}
