package struts;
import java.math.BigInteger;

public class PubKey{
	public BigInteger modulus;
	public BigInteger encryptionExponent;
	
	public PubKey(BigInteger a, BigInteger b){
		this.modulus = a;
		this.encryptionExponent = b;
	}
	
	public PubKey create(BigInteger a, BigInteger b){
		return new PubKey(a, b);
	}
}