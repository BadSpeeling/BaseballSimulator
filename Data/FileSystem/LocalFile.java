package FileSystem;

public class LocalFile {
	
	private String path; //directories differntiated with a single \ and no \ to end
	private String fileName;
	
	public LocalFile (String path, String fileName) {
		this.path = path;
		this.fileName = fileName;
	}
	
	public LocalFile (String path) {
		this.path = path;
		this.fileName = "";
	}
 
	public String getPath() {
		return path;
	}

	public String getFileName() {
		return fileName;
	}
	
	public void setFileName (String name) {
		this.fileName = name;
	}
	
	public String getFullPath () {
		return path + "\\" + fileName;
	}
	
	public String getFile (String fileName) {
		return path + "\\" +fileName;
	}
	
}
