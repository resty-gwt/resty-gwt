/**
 * 
 */
package org.fusesource.restygwt.client.util;

import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;


/**
 * @author Larkin Lowrey
 *
 */
public class Base64Codec
{
   public static byte[] decode(String base64)
   {
       Logger log = Logger.getLogger("base64 decode");
       log.info("encoded: [" + base64.length() + "] " + base64);
       String decoded = jsDecode(base64);
       byte bytes[];
       try
       {
           bytes = decoded.getBytes("ISO-8859-1");
       }
       catch (UnsupportedEncodingException e)
       {
           throw new RuntimeException("Unable to decode base64", e);
       }
       
       StringBuilder sb = new StringBuilder();
       sb.append("encoded: [");
       sb.append(bytes.length);
       sb.append("] ");
       
       char HEX[] = "0123456789ABCDEF".toCharArray();
       for (int i = 0; i < bytes.length; i++)
       {
           sb.append(HEX[(bytes[i] >> 4) & 0x0F]);
           sb.append(HEX[(bytes[i] >> 0) & 0x0F]);
       }
       log.info(sb.toString());
       
       return bytes;
   }
   
   public static String encode(byte bytes[])
   {
       try
       {
           return jsEncode(new String(bytes, "ISO-8859-1"));
       }
       catch (UnsupportedEncodingException e)
       {
           throw new RuntimeException("Unable to encode to base64", e);
       }
   }
   
   private static native String jsDecode(String base64) /*-{
       if (window.atob)
       {
           return atob(base64);
       }
       else
       {
           if (base64.length == 0)
               return "";
               
           var mod = base64.length % 4;
           var encoded;
           if (mod == 0)
               encoded = base64;
           else if (mod == 1)
               throw "Illegal base64 length";
           else if (mod == 2)
               encoded = base64.concat("==");
           else
               encoded = base64.concat("=");
           var wholeBlocks = encoded.length / 4;
           if (encoded.charAt(encoded.length - 1) == '=' || encoded.charAt(encoded.length - 2) == '=')
               wholeBlocks--;
           
           var lookup = [
               -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
               -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
               -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,62,-1,-1,-1,63,
               52,53,54,55,56,57,58,59,60,61,-1,-1,-1,-1,-1,-1,
               -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9,10,11,12,13,14,
               15,16,17,18,19,20,21,22,23,24,25,-1,-1,-1,-1,-1,
               -1,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,
               41,42,43,44,45,46,47,48,49,50,51,-1,-1,-1,-1,-1 
           ];
           var decoded = "";           
           
           for (i = 0, j = 0; j < wholeBlocks; j++)
           {
               var c1 = encoded.charCodeAt(i++);
               var c2 = encoded.charCodeAt(i++);
               var c3 = encoded.charCodeAt(i++)
               var c4 = encoded.charCodeAt(i++);
               
               var e1 = 0 <= c1 && c1 <= 128 ? lookup[c1] : -1;
               var e2 = 0 <= c2 && c2 <= 128 ? lookup[c2] : -1;
               var e3 = 0 <= c3 && c3 <= 128 ? lookup[c3] : -1;
               var e4 = 0 <= c4 && c4 <= 128 ? lookup[c4] : -1;
               
               if (e1 == -1 || e2 == -1 || e3 == -1 || e4 == -1)
                   throw "Illegal base64 characters";
               
               var d1 = String.fromCharCode(((e1 << 2) | (e2 >> 4)) & 0xFF);
               var d2 = String.fromCharCode(((e2 << 4) | (e3 >> 2)) & 0xFF);
               var d3 = String.fromCharCode(((e3 << 6) | (e4 >> 0)) & 0xFF);
               decoded = decoded.concat(d1, d2, d3);
           }
           
           if (i < encoded.length)
           {
               var c1 = encoded.charCodeAt(i++);
               var c2 = encoded.charCodeAt(i++);
               var c3 = encoded.charCodeAt(i++)
               
               var e1 = 0 <= c1 && c1 <= 128 ? lookup[c1] : -1;
               var e2 = 0 <= c2 && c2 <= 128 ? lookup[c2] : -1;
               
               if (e1 == -1 || e2 == -1)
                   throw "Illegal base64 characters";

               var d1 = String.fromCharCode(((e1 << 2) | (e2 >> 4)) & 0xFF);
               if (c3 != "=".charCodeAt(0))
               {
                   var e3 = 0 <= c3 && c3 <= 128 ? lookup[c3] : -1;
                   if (e3 == -1)
                       throw "Illegal base64 characters";
                   var d2 = String.fromCharCode(((e2 << 4) | (e3 >> 2)) & 0xFF);
                   decoded = decoded.concat(d1, d2);
               }
               else
                   decoded = decoded.concat(d1);
           }
           
           return decoded;
       }
   }-*/;

   private static native String jsEncode(String bytes) /*-{
       if (window.btoa)
       {
           return btoa(bytes);
       }
       else
       {
           if (bytes.length == 0)
               return "";
               
           var encoded = "";
           var wholeBlocks = Math.floor(bytes.length / 3);
           
           var lookup = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
           
           for (i = 0, j = 0; j < wholeBlocks; j++)
           {
               var b1 = bytes.charCodeAt(i++);
               var b2 = bytes.charCodeAt(i++);
               var b3 = bytes.charCodeAt(i++);
               
               var e1 = b1 >> 2; 
               var e2 = (b1 << 4 | b2 >> 4); 
               var e3 = (b2 << 2) | (b3 >> 6); 
               var e4 = b3;
               
               var c1 = lookup.charAt(e1 & 0x3F); 
               var c2 = lookup.charAt(e2 & 0x3F); 
               var c3 = lookup.charAt(e3 & 0x3F); 
               var c4 = lookup.charAt(e4 & 0x3F);
               
               encoded = encoded.concat(c1, c2, c3, c4); 
           }
           
           var rem = bytes.length - i;
           if (rem > 0)
           {
               var b1 = bytes.charCodeAt(i++);
               var b2 = rem == 2 ? bytes.charCodeAt(i++) : 0;
               
               var e1 = (b1 >> 2) & 0x3F; 
               var e2 = (b1 << 4 | b2 >> 4) & 0x3F; 
               var c1 = lookup.charAt(e1);
               var c2 = lookup.charAt(e2); 
               
               if (rem == 2)
               {
                   var e3 = (b2 << 2) | (b3 >> 6) & 0x3F; 
                   var c3 = lookup.charAt(e3); 
                   
                   encoded = encoded.concat(c1, c2, c3, "=");                  
               }
               else
                   encoded = encoded.concat(c1, c2, "==");                 
           }
           
           return encoded;
       }
   }-*/;
}
