package pl.otwartezabytki.android.tools;

import android.util.DisplayMetrics;

public class ResolutionDependent {

	public static Integer getThumbnailDimensions(int density){
		//int density= getResources().getDisplayMetrics().densityDpi;

		switch(density){
		case DisplayMetrics.DENSITY_LOW:
			return Integer.valueOf(53);
	  
		case DisplayMetrics.DENSITY_MEDIUM:
			return Integer.valueOf(70);
		         
		case DisplayMetrics.DENSITY_HIGH:
			return Integer.valueOf(105);
		    
		case DisplayMetrics.DENSITY_XHIGH:
			return Integer.valueOf(140);
		         
		case DisplayMetrics.DENSITY_XXHIGH:
			return Integer.valueOf(210);
		      
		case DisplayMetrics.DENSITY_TV:
			return Integer.valueOf(140);
        
		default:
			return Integer.valueOf(70);
		}
		
	}
	
	public static Integer getDetailImageDimensions(int density){
		int base = 110;
		switch(density){
		case DisplayMetrics.DENSITY_LOW:
			return Integer.valueOf((int)0.75*base);
	  
		case DisplayMetrics.DENSITY_MEDIUM:
			return Integer.valueOf(base);
		         
		case DisplayMetrics.DENSITY_HIGH:
			return Integer.valueOf((int)1.5*base);
		    
		case DisplayMetrics.DENSITY_XHIGH:
			return Integer.valueOf(2*base);
		         
		case DisplayMetrics.DENSITY_XXHIGH:
			return Integer.valueOf(3*base);
		      
		case DisplayMetrics.DENSITY_TV:
			return Integer.valueOf(2*base);
        
		default:
			return Integer.valueOf(base);
		}
	}
	
	public static Integer getStaticMapDimensions(int density){
		int base = 110;
		switch(density){
		case DisplayMetrics.DENSITY_LOW:
			return Integer.valueOf((int)0.75*base);
	  
		case DisplayMetrics.DENSITY_MEDIUM:
			return Integer.valueOf(base);
		         
		case DisplayMetrics.DENSITY_HIGH:
			return Integer.valueOf((int)1.5*base);
		    
		case DisplayMetrics.DENSITY_XHIGH:
			return Integer.valueOf(2*base);
		         
		case DisplayMetrics.DENSITY_XXHIGH:
			return Integer.valueOf(3*base);
		      
		case DisplayMetrics.DENSITY_TV:
			return Integer.valueOf(2*base);
        
		default:
			return Integer.valueOf(base);
		}
	}
}
