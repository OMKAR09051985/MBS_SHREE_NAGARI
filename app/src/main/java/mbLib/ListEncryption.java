package mbLib;
public class ListEncryption 
{
	static int intArray[]={14,9,12,11,7,13,16,5,17,12,9,15,18,14,19,11,16,12,18,10,20,15,12,19,25,14,28,7,16,11};
	static String charSet="`1234567890-=[];,./ABCDEFGHIJKLMNOPQRSTUVWXYZ~!@#$%^*()_+abcdefghijklmnopqrstuvwxyz{}|:?";
	public static String encryptData(String str)
		{//2
			//str=str.toUpperCase();
			//System.out.println("charSet\n"+charSet+"\nlength="+charSet.length());
			String retStr="";
			for (int i=0;i<str.length();i++)
			{//3
				int ch=charSet.indexOf(""+str.charAt(i));
				//System.out.println(str.charAt(i)+" is ch="+ch+"th character");
				int ch1;
				if(i%2==0)
				{//4
					ch1=ch+intArray[i%intArray.length];
					//System.out.println("ch1="+ch1);
					if(ch1>=charSet.length())
					{//5
						ch1=ch1-charSet.length();
						//System.out.println("ch1="+ch1);
					}//5
				}//4
				else
				{//4
					ch1=ch-intArray[i%intArray.length];
					//System.out.println("ch1="+ch1);
					if(ch1<0)
					{//5
						ch1=charSet.length()+ch1;
						//System.out.println("ch1="+ch1);
					}//5
				}//4
				char v_ch=charSet.charAt(ch1);
				//System.out.println("v_ch="+v_ch);
				retStr=retStr+v_ch;
			}//3
			return retStr;
			//return new BASE64Encoder().encode(retStr.getBytes());
	}//2
}
