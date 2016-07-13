package me.kalehv.popmovie.global;

/**
 * Created by harshadkale on 4/10/16.
 */
public class C {
    public static final String MOVIE_DATABASE_NAME = "popmovie.db";

    // http://api.themoviedb.org/3/movie/top_rated?api_key=e19c269eefc0509a3cb2e153c839d590
    public static final String API_KEY_QUERY_PARAM = "api_key";
    public static final String THE_MOVIES_DB_BASE_URL = "http://api.themoviedb.org/";
    public static final String POSTER_IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w500";
    public static final String BACKDROP_IMAGE_BASE_URL = "http://image.tmdb.org/t/p/original";
    public static final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch";
    public static final String YOUTUBE_QUERY_PARAM = "v";
    public static final String VIDEOS_YOUTUBE_KEY_NAME = "key";
    public static final String YOUTUBE_FORCE_FULLSCREEN = "force_fullscreen";

    // Parceler Keys
    public static final String MOVIE_PARCEL = "MOVIE_PARCEL";

    public static final String[] SELECT_ALL_COLUMNS = new String[]{"*"};
}
