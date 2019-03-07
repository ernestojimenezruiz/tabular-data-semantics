package uk.turing.aida;

public class AIDACSVFile {

	private String dataset;
	private String path;
	private String file;
	
	public AIDACSVFile(String dataset, String path, String file) {
		this.setDataset(dataset);
		this.setPath(path);
		this.setFile(file);
	}

	public String getDataset() {
		return dataset;
	}

	public void setDataset(String dataset) {
		this.dataset = dataset;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}
	
}
