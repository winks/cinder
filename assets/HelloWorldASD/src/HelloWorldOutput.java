import java.util.ArrayList;

public class HelloWorldOutput {
	public void say() {
		ArrayList<String> al = new ArrayList<String>();
		al.add("Hello, ");
		al.add("World!");
		al.add("\n");
		for (String s : al) {
			System.out.print(s);
			System.out.print(s);
		}
	}
}
