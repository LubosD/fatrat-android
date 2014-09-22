/*
FatRat download manager
http://fatrat.dolezel.info

Copyright (C) 2006-2011 Lubos Dolezel <lubos a dolezel.info>

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
version 2 as published by the Free Software Foundation.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/
package info.dolezel.fatrat.plugins.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author lubos
 */
public final class FormatUtils {
    private static final Pattern reFileSize = Pattern.compile("(\\d+[\\d\\.,]+)\\s*(b|ki?b|mi?b|gi?b|ti?b|pi?b)", Pattern.CASE_INSENSITIVE);
    
    private FormatUtils() {
    }
    
    /**
     * Formats the file size as a string, e.g. 1024 -> 1 KB etc.
     * @param bytes The number of bytes
     * @return A formatted string.
     */
    public static String formatSize(long bytes) {
        if (bytes < 1024)
            return bytes + " B";
        else if (bytes < 1024*1024)
            return singleDecimalDigit(bytes/1024.0) + " KB";
        else if (bytes < 1024*1024*1024)
            return singleDecimalDigit(bytes/1024.0/1024.0) + " MB";
        else
            return singleDecimalDigit(bytes/1024.0/1024.0/1024.0) + " GB";
    }
    
    /**
     * Returns the number as a string with a single decimal digit.
     */
    public static String singleDecimalDigit(double d) {
        return new DecimalFormat("#.#").format(d);
    }
    
    /**
     * Formats the time information as a string.
     * @param seconds The number of seconds.
     * @return A formatted string.
     */
    public static String formatTime(int seconds) {
        StringBuilder result = new StringBuilder();
        int days,hrs,mins,secs;
        days = seconds/(60*60*24);
        seconds %= 60*60*24;

        hrs = seconds/(60*60);
        seconds %= 60*60;

        mins = seconds/60;
        secs = seconds%60;

        if (days > 0)
            result.append(days).append("d ");
        if (hrs > 0)
            result.append(hrs).append("h ");
        if (mins > 0)
            result.append(mins).append("m ");
        if (secs > 0)
            result.append(secs).append("s ");

        return result.toString();
    }
    
    /**
     * Attempts to find file size information in the string given.
     * @param str The string to be searched and parsed.
     * @return The file size in bytes or -1 if failed.
     */
    public static long parseSize(String str) {
        Matcher m = reFileSize.matcher(str);
        if (!m.find())
            return -1;
        
        String number = m.group(1).replace(',', '.');
        String unit = m.group(2).toLowerCase().replace("i", "");
        
        double result = Double.parseDouble(number);
        if (unit.equals("b"))
            ;
        else if (unit.equals("kb"))
            result *= 1024;
        else if (unit.equals("mb"))
            result *= 1024l*1024l;
        else if (unit.equals("gb"))
            result *= 1024l*1024l*1024l;
        else if (unit.equals("tb"))
            result *= 1024l*1024l*1024l*1024l;
        else if (unit.equals("pb"))
            result *= 1024l*1024l*1024l*1024l*1024l;
        else
            result = -1;
        
        return (long) result;
    }

    /**
     * @return The pattern used by {@link #parseSize}.
     */
    public static Pattern getFileSizePattern() {
        return reFileSize;
    }
    
    public static Map<String,String> parseQueryString(String input) {
        try {
            Map<String,String> rv = new HashMap<String,String>();
            String[] parts = input.split("&");

            for (String part : parts) {
                String[] pair = part.split("=", 2);
                if (pair.length != 2)
                    continue;
                
                String name = URLDecoder.decode(pair[0], "UTF-8");
                String value = URLDecoder.decode(pair[1], "UTF-8");
                
                rv.put(name, value);
            }
            
            return rv;
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }
    
    
}
