package FileSystem;

public class LocalFile {
	
	private String path; //directories differntiated with a single \ and no \ to end
	private String fileName;
	
	public LocalFile (String path, String fileName) {
		this.path = path;
		this.fileName = fileName;
	}

	public String getPath() {
		return path;
	}

	public String getFileName() {
		return fileName;
	}
	
	public String getFullPath () {
		return path + "\\" + fileName;
	}
	
}
