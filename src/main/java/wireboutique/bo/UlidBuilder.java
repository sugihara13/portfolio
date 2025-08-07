package wireboutique.bo;

import java.security.SecureRandom;
import java.time.Instant;

public class UlidBuilder {

	public UlidBuilder() {
	}
	
	public String buildId() {
		//get rondom
		SecureRandom rand = new SecureRandom();
		byte random[] = new byte[10];
		rand.nextBytes(random);
		
		//get time
		Instant now = Instant.now();
		long epochSec = now.getEpochSecond();
		
		long timeStampMAX = 0xffffffffffffl;
		if(epochSec >  timeStampMAX) {
			//outrange timestamp
		}
		
		long id[]= {0,0};
		
		long timestamp = epochSec & timeStampMAX;
		
		//System.out.println("byte timestamp : "+Long.toBinaryString(timestamp));
		
		id[1] = timestamp << 16;
		{
			long t = random[9];
			t=t & 0xffl;
			t = t << 8 ;
			
			long t2 = random[8];
			t2 = t2 & 0xffl;
			t = t|t2;
			
			id[1] = id[1] | t;
		}
		//System.out.println("id[1] 128~65bit: "+Long.toBinaryString(id[1]));
		
		id[0] = id[0] & 0x0l ;
		
		for(int i=7;i>0;i--) {
			long t = random[i];
			t=t & 0xffl;
			
			id[0] = id[0] | t;
			id[0] = id[0] << 8;
		}
		
		{
			long t = random[0];
			t=t & 0xffl;
		
			id[0] = id[0] | t;
		}
		
		//System.out.println("id[0] 64~1bit: "+Long.toBinaryString(id[0]));
		
		
		//System.out.println("encode ulid: "+Base32Encode(id));
		return Base32Encode(id);
	}
	
	public static String Base32Encode(long id[]) {
		char base[]={'0','1','2','3','4','5','6','7','8','9',
				'A','B','C','D','E','F','G','H','J','K','M','N','P','Q','R','S','T','V','W','X','Y','Z'};
		
		StringBuilder sb=new StringBuilder();
		long mask = 0xf800000000000000l;
		
		//Encode timestamp
		//48~45bit
		for(int i=0;i<9;i++) {
			Long l = id[1] & mask;
		
			l >>>= 59 - 5*i;
			
			sb.append(base[l.intValue()]);
			mask >>>= 5;
		}
		
		//3~1bit
		{
			Long l = id[1] & 0x70000l;
			l >>>= 16;
			//padding bit
			l <<= 2;
			
			sb.append(base[l.intValue()]);
		}
		
		//System.out.println("encoded Timestamp: "+sb);
		
		//Encode random
		//80~66bit
		mask = 0xf800l;
		for(int i=0;i<3;i++) {
			Long l = id[1] & mask;
			l >>>= 11 - 5*i;

			sb.append(base[l.intValue()]);
			mask >>>= 5;
		}
		
		//65~60bit
		{
			Long l1 = id[1] & 1l;
			Long l2 = id[0]	& 0xf000000000000000l;
			
			l2	=	l2>>>60;
			
			l1 = (id[1] & 1l) | l2;
			
			sb.append(base[l1.intValue()]);
		}
		//60~1bit
		mask = 0xf80000000000000l;
		for(int i=0;i<12;i++) {
			Long l = id[0] & mask;
		
			l >>>= 55 - 5*i;
			
			sb.append(base[l.intValue()]);
			mask >>>= 5;
		}
		return sb.toString();
	}
}
