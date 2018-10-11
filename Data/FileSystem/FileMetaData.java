package FileSystem;

public class FileMetaData {
	
	private String fileName;
	private String fileHeader;
	
	public FileMetaData(String fileName, String fileHeader) {
		this.fileName = fileName;
		this.fileHeader = fileHeader;
	}

	public String getFileName() {
		return fileName;
	}

	public String getFileHeader() {
		return fileHeader;
	}
	
}
