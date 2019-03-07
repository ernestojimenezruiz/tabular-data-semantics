package uk.turing.aida;

import java.util.ArrayList;
import java.util.List;

public class AIDADatasets {

	protected static String base_path="/home/ejimenez-ruiz/Documents/ATI_AIDA/Datasets/AnalysedDatasets/";
	
	
	protected static List<AIDACSVFile> csv_files = new ArrayList<AIDACSVFile>();
	
	protected static String trait_dataset = "TraitHub";
	protected static String broadband_dataset = "Broadband";
	protected static String hes_dataset = "HES";//Household Electricity Survey HES Dataset
	
	
	protected static String ground_truth = "GT_AIDA_datasets.csv";
	
	
	public static String getGTFile() {
		return base_path + ground_truth;
	}
	
	
	public static String getBasePath(){
		return base_path;
	}
	
	
	public static List<AIDACSVFile> getCSVFiles(){
		return csv_files;
	}
	
	
	public static void setAIDADatastets() {
		
		
		//There are 20 CSV files that are relevant
	
		//TraitHub dataset: 1
		csv_files.add(
				new AIDACSVFile(
						trait_dataset,
						//base_path + "TraitHub/data_final/",
						base_path + "TraitHub/",
						"TTT_cleaned_dataset_final.csv"));
		//dataset = "TraitHub";
		//path = "/home/ejimenez-ruiz/Documents/ATI_AIDA/Datasets/TraitHub/data_final/";
		//file = "TTT_cleaned_dataset_final.csv";
		
		
		
		//Braodband CSV files
		//2
		csv_files.add(
				new AIDACSVFile(
						broadband_dataset,
						//base_path + "broadband/2013-11/",
						base_path + "Broadband/",
						"november_2013.csv"));
		
		//3
		csv_files.add(
				new AIDACSVFile(
						broadband_dataset,
						//base_path + "broadband/2014-02/",
						base_path + "Broadband/",
						"sk_bb_speeds_feb_data.csv"));
		
		
		//4
		csv_files.add(
				new AIDACSVFile(
						broadband_dataset,
						//base_path + "broadband/2014-05/"
						base_path + "Broadband/",
						"panellist_data_may_2014.csv"));
		
		//5
		csv_files.add(
				new AIDACSVFile(
						broadband_dataset,
						//base_path + "broadband/2014-11/",
						base_path + "Broadband/",
						"panellist_data_november_2014.csv"));
		
		
		
		//6
		csv_files.add(
				new AIDACSVFile(
						broadband_dataset,
						//base_path + "broadband/2015-11/",
						base_path + "Broadband/",
						"panellist_data_november_2015.csv"));
		
		
		
		
		//7
		csv_files.add(
				new AIDACSVFile(
						broadband_dataset,
						//base_path + "broadband/2016-11/",
						base_path + "Broadband/",
						"November-2016-Panellist-data-fixed.csv"));
		
		
		//8
		csv_files.add(
				new AIDACSVFile(
						broadband_dataset,
						//base_path + "broadband/2017-08/",
						base_path + "Broadband/",
						"august-2017-panellist.csv"));
		
		
		
		//HES CSV files
		//9
		csv_files.add(
				new AIDACSVFile(
						hes_dataset,
						//base_path + "HES/small/",
						base_path + "HES/",
						"appliance_codes.csv"));
		
		
		
		//10
		csv_files.add(
				new AIDACSVFile(
						hes_dataset,
						//base_path + "HES/small/",
						base_path + "HES/",
						"appliance_type_codes.csv"));
		
		
		//11
		csv_files.add(
				new AIDACSVFile(
						hes_dataset,
						//base_path + "HES/anonhes/",
						base_path + "HES/",
						"ipsos-anonymised-corrected_310713.csv"));
		
		
		//12
		csv_files.add(
				new AIDACSVFile(
						hes_dataset,
						//base_path + "HES/anonhes/",
						base_path + "HES/",
						"rdsap_glazing_details_anon.csv"));
		
		
		//13
		csv_files.add(
				new AIDACSVFile(
						hes_dataset,
						//base_path + "HES/anonhes/",
						base_path + "HES/",
						"rdsap_public_anon.csv"));
		
		
		
		//14
		csv_files.add(
				new AIDACSVFile(
						hes_dataset,
						//base_path + "HES/appdata/",
						base_path + "HES/",
						"appliance_data.csv"));
		
		
		
		//It will probably require contextual information...
		//Quite challenging
		
		//15
		csv_files.add(
				new AIDACSVFile(
						hes_dataset,
						//base_path + "HES/originalhes/",
						base_path + "HES/",
						"appliance_groups.csv"));
		
		
		//16
		csv_files.add(
				new AIDACSVFile(
						hes_dataset,
						//base_path + "HES/originalhes/",
						base_path + "HES/",
						"diary_tumble_dryer.csv"));
		
		
		//17
		csv_files.add(
				new AIDACSVFile(
						hes_dataset,
						//base_path + "HES/originalhes/",
						base_path + "HES/",
						"diary_washer_dryer.csv"));
		
		
		//18
		csv_files.add(
				new AIDACSVFile(
						hes_dataset,
						//base_path + "HES/originalhes/",
						base_path + "HES/",
						"diary_washing_machine.csv"));
		
		//19
		csv_files.add(
				new AIDACSVFile(
						hes_dataset,
						//base_path + "HES/originalhes/",
						base_path + "HES/",
						"diary_dish_washer.csv"));
		
		//20
		csv_files.add(
				new AIDACSVFile(
						hes_dataset,
						//base_path + "HES/originalhes/",
						base_path + "HES/",
						"diary_oven.csv"));
		
	}
	
	
}
