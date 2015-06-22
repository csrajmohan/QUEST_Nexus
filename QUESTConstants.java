package quest;
import java.awt.Color;
import java.util.Vector;

public class QUESTConstants 
{

	/** LOADPACKET **/
	public static boolean IS_PKT_LOADED = false;
	
	
	/**EXPAND**/
	public static String prefixQueryStr = 		 "  ROBUSTPLAN (\""+ QUESTConstants.lambda_l +"\",\""+ QUESTConstants.lambda_g +"\", ";
	public static String prefixQueryStrFPC =     "  ROBUSTPLAN (\"F"+ QUESTConstants.lambda_l +"\",\""+ QUESTConstants.lambda_g +"\", ";
//	public static String prefixQueryStrFPC = " ROBUSTPLAN (\"ORIG\",\""+ PicassoConstants.lambda_g +"\", ";
	
	public static String PKT_NAME = "QT10_DEC-ORIG.apkt";          // this is the name of the original packet while generating prefacto diagram
	
	public static boolean isPrefacto = false;
//	public static boolean isPrefacto = true;                                 // to ask for ROBUST plan
   
	public static boolean IS_POSTGRES_FPC_ENABLED = true;				         // to be able to save pcst files 

	public static double lambda_l = 20;
	public static double lambda_g = 20;
	/**EXPAND**/	

	
	
	
	/**TOP-K**/
//	public static boolean TOP_K = true;
	public static boolean TOP_K = false;
	public static boolean lookingExtraPlans = false;
	public static boolean topkquery = false;
	public static boolean saveExtraPlans = true;
	public static int numExtraPlans = 3;                      // wrongly named - it means  (num of Extra plans + 1) 
	public static int currentExtraPlanNum = 0;
	public static double otherPlanCosts[] = new double[numExtraPlans-1];       // assuming max 10 plans
	public static double inflationRatio = 1.0;	
	/**TOP-K**/
	
		
	
	/**FULL-ROBUSTNESS**/
	/* unused */
	public static boolean identified_New_Plan = false;
	public static boolean JSP_SINGLE_PLAN = false, JSP_SINGLE_PLAN_TYPE2 = false;
	public static String JSP_plan = "\"G((S3)H(((I1)M(S2))H(S4)))\"";

	public static double jSelecForPlanLoc1 = 1.0, jSelecForPlanLoc2 = 1.0, jSelecForPlanLoc3 = 1.0, jSelecForPlanLoc4 = 1.0, jSelecForPlanLoc5 = 1.0, jSelecForPlanLoc6 = 1.0;		// for pcst of a plan - not need to be specified for current experiment - original(SP-2)  + orig (JSP-2) + middle (JSP-2) + top (JSP-2)
	public static int PSPLoc1 = 100, PSPLoc2 = 100, PSPLoc3 = 100, PSPLoc4 = 100, PSPLoc5 = 100, PSPLoc6 = 100;																			 // orig - middle - top (if JSP diagram = true) // also if JSP_PLAN_LOCS is true - which constant base-sel location (%) to choose in preds  - to generate JS diagrams
	/* unused */
	
	
	/* global*/
//	public static boolean JSP_Diagram = true;
	public static boolean JSP_Diagram = false;

	public static String SAVE_PATH = "/home/dsladmin/project/output/";
	public static String JSP_PATH = "/home/dsladmin/project/output/";               // used where we convert files from .txt to .pcst
	
	public static boolean JSP_PLAN_INFO = true, JSP_PLANS_LOCS = true, JSP_ALL_PLANS_COSTS = true;              // shows the query to be used for getting cost diagram for each plan
//	public static boolean JSP_CALCULATE_ALL_PLANS_COSTS = true, isSAVE = true;									//save cost and packet files or not
	public static boolean JSP_CALCULATE_ALL_PLANS_COSTS = false, isSAVE = true;

	/* in generation */
	public static String JSPConstant[] = {"100","100","100","100","100","100","100","100","100"};
	public static double jSelec[] = {1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0};					   // will be calculated in the generation loop
	/* in generation */

	/* global*/
	

	public static int JSP_ExpoDistributionBase = 2;
//	public static int JSP_ExpoDistributionBase = -1;
	public static char JSP_Distribution = 'E';
//	public static char JSP_Distribution = 'U';

	/* memory dimension */
	
//	public static boolean use_new_parameters = true;
	public static boolean use_new_parameters = false;
	
	// for 100GB database
//	public static String work_memory_est = "512MB";
//	public static String effective_cache_size_est = "10GB";

	// for 1GB data
	public static String work_memory_est = "100MB";
	public static String effective_cache_size_est = "2GB";

	public static String random_page_cost_est = "2.0";
	public static String cpu_operator_cost_est = "0.0005";            // 0.0025
	public static String cpu_index_tuple_cost_est = "0.001";         // 0.005
	public static String cpu_tuple_cost_est = "0.002";               // 0.01
	
	
	
	
	
	// start count with 0 and no need to consider for memory dimension - it is already taken care of - april 2012
	public static int firstDimensionMemory = 0;      // 1 means that first dimension is memory dimension , 0 means not
	public static int work_mem[] = {200,150,100,50,40,30,20,10,5,1};                            // in MB
	public static int effective_cache_size[] = {4096,3072,2048,1536,1024,512,256,128,64,16};       // in MB
	/* memory dimension */


	
	public static String variesJSP = "";                        
	public static int JS_multiplier_dimension[] = {-1,-1,-1,-1,-1};  // initial 2 are join-dimension and then FK side
	public static String JS_multiplier[] = {"25","-1","-1","-1","-1","-1","-1","-1","-1"};			
	public static String varyingBaseRelations[] = {};
	public static String fpcConstants[] = {};

		
	
	//TPCH-Q10-2D - one base (L) one join (OL) predicates
//	public static String variesJSP = "23";                        
//	public static int JS_multiplier_dimension[] = {-1,-1,-1,-1,-1};  // initial 2 are join-dimension and then FK side
////	public static String JS_multiplier[] = {"25","-1","-1","-1","-1","-1","-1","-1","-1"};  2D			
//	public static String JS_multiplier[] = {"190","-1","-1","-1","-1","-1","-1","-1","-1"};
//	public static String varyingBaseRelations[] = {"customer"};
//	public static String fpcConstants[] = {"10000.0"};
	
	//TPCH-Q10-2D
//	public static String variesJSP = "1223";                        
//	public static int JS_multiplier_dimension[] = {-1,-1,-1,-1,-1};  // initial 2 are join-dimension and then FK side
//	public static String JS_multiplier[] = {"1","25","-1","-1","-1","-1","-1","-1","-1"};			
//	public static String varyingBaseRelations[] = {"customer"};
//	public static String fpcConstants[] = {"10000.0"};

		

	//TPCH-Q5-3D
//	public static String variesJSP = "122334";                        
//	public static int JS_multiplier_dimension[] = {-1,-1,-1,-1,-1};  // initial 2 are join-dimension and then FK side
//	public static String JS_multiplier[] = {"1","100","1","-1","-1","-1","-1","-1","-1"};			
//	public static String varyingBaseRelations[] = {"supplier"};
//	public static String fpcConstants[] = {"10000.0"};

	//TPCH-Q7-3D
//	public static String variesJSP = "233445";                        
//	public static int JS_multiplier_dimension[] = {-1,-1,-1,-1,-1};  // initial 2 are join-dimension and then FK side
//	public static String JS_multiplier[] = {"1","1","1","-1","-1","-1","-1","-1","-1"};			
//	public static String varyingBaseRelations[] = {"supplier"};
//	public static String fpcConstants[] = {"10000.0"};
	
//	//TPCH-Q7-5D
//	public static String variesJSP = "2334452657";                        
//	public static int JS_multiplier_dimension[] = {-1,-1,-1,-1,-1};  // initial 2 are join-dimension and then FK side
//	public static String JS_multiplier[] = {"1","1","1","25","25","-1","-1","-1","-1"};			
//	public static String varyingBaseRelations[] = {"supplier"};
//	public static String fpcConstants[] = {"10000.0"};

	//TPCH-Q8-4D
//	public static String variesJSP = "24344556";                      // orders - 10 %, part - 0.5 %  
//	public static int JS_multiplier_dimension[] = {-1,-1,-1,-1,-1};  // initial 2 are join-dimension and then FK side
//	public static String JS_multiplier[] = {"200","1","10","1","-1","-1","-1","-1","-1"};			
//	public static String varyingBaseRelations[] = {"lineitem"};
//	public static String fpcConstants[] = {"100000.0"};

	//TPCH-Q8-2D
//	public static String variesJSP = "2445";                      // orders - 10 %, part - 0.5 %  
//	public static int JS_multiplier_dimension[] = {-1,-1,-1,-1,-1};  // initial 2 are join-dimension and then FK side
//	public static String JS_multiplier[] = {"200","10","-1","-1","-1","-1","-1","-1","-1"};			
//	public static String varyingBaseRelations[] = {"lineitem"};
//	public static String fpcConstants[] = {"100000.0"};


	//DS-Q7-4D
//	public static String variesJSP = "12131415";                        
//	public static int JS_multiplier_dimension[] = {-1,-1,-1,-1,-1};  // initial 2 are join-dimension and then FK side
//	public static String JS_multiplier[] = {"66","200","1","1","-1","-1","-1","-1","-1"};			
//	public static String varyingBaseRelations[] = {"store_sales"};
//	public static String fpcConstants[] = {"197.5"};

	//DS-Q15-3D
//	public static String variesJSP = "141223";                        
//	public static int JS_multiplier_dimension[] = {-1,-1,-1,-1,-1};  // initial 2 are join-dimension and then FK side
//	public static String JS_multiplier[] = {"200","1","6","-1","-1","-1","-1","-1","-1"};			
//	public static String varyingBaseRelations[] = {"catalog_sales"};
//	public static String fpcConstants[] = {"150.5"};


//	//DS-Q96-3D
//	public static String variesJSP = "121314";                        
//	public static int JS_multiplier_dimension[] = {-1,-1,-1,-1,-1};  // initial 2 are join-dimension and then FK side
//	public static String JS_multiplier[] = {"9.5","48","7","-1","-1","-1","-1","-1","-1"};			
//	public static String varyingBaseRelations[] = {"store_sales"};
//	public static String fpcConstants[] = {"197.5"};	
	

	//DS-Q19-5D
//	public static String variesJSP = "1213141645";                        
//	public static int JS_multiplier_dimension[] = {-1,-1,-1,-1,-1};  // initial 2 are join-dimension and then FK side
//	public static String JS_multiplier[] = {"2000","110","1","1","1","-1","-1","-1","-1"};			
//	public static String varyingBaseRelations[] = {"store_sales"};
//	public static String fpcConstants[] = {"197.5"};	


//	//DS26_4D
//	public static String variesJSP = "12131415";                        
//	public static int JS_multiplier_dimension[] = {-1,-1,-1,-1,-1};                                // initial 2 are join-dimension and then FK side
//	public static String JS_multiplier[] = {"67","200","1","1","-1","-1","-1","-1","-1"};			
//	public static String varyingBaseRelations[] = {"item"};
//	public static String fpcConstants[] = {"100"};	


	//DS91_4D
//	public static String variesJSP = "23454647";                        
//	public static int JS_multiplier_dimension[] = {-1,-1,-1,-1,-1};                                // initial 2 are join-dimension and then FK side
//	public static String JS_multiplier[] = {"2500","9","18","6","-1","-1","-1","-1","-1"};			
//	public static String varyingBaseRelations[] = {"customer_address"};
//	public static String fpcConstants[] = {"-7"};	

	
	
	public static int JSPdimension = variesJSP.length()/2;
	

	
	
	

	
	
	/**FULL-ROBUSTNESS**/	
	
	
	
	//	USER SETTABLE CONSTANTS 

	public static int SERVER_PORT = 4444;
	public static boolean LOW_VIDEO = false;
	// Thresholds
	public static double SELECTIVITY_LOG_REL_THRESHOLD = 10;
	public static double SELECTIVITY_LOG_ABS_THRESHOLD = 1;

	public static double PLAN_REDUCTION_THRESHOLD = 10;
	public static double COST_DOMINATION_THRESHOLD = 95;
	
	// Used by range-res. Do not touch these variables.
	public static int NUM_DIMS=7;
	public static int a[]={0,1};
	public static double MINIMUMRANGE=1;
	public static int []slicePlans;
	public static double []sgpercs;
	public static boolean IS_SUSPENDED; // Assuming no one would use more than MAX_CLIENTS at once.
	// public static boolean SAVING_DONE = false;

	// Reduction Algorithm
	public static final int REDUCE_AG = -1;//Removed from GUI
	public static final int REDUCE_CG = 1;
	public static final int REDUCE_CGFPC = -1;//Removed from GUI
	public static final int REDUCE_SEER = -1;//Removed from GUI
	public static final int REDUCE_CCSEER = 2;
	public static final int REDUCE_LITESEER = 3;
	
	public static int REDUCTION_ALGORITHM = REDUCE_CG;
	public static int DESIRED_NUM_PLANS = 10;

	public static boolean SAVE_COMPRESSED_PACKET = true;

	// Produce debugging ouptut
	public static boolean IS_CLIENT_DEBUG = false;
	public static boolean IS_SERVER_DEBUG = false;
	
	// On engines that support XML plans, whether to generate and save XML plan strings during diagram generation
	public static boolean SAVE_XML_INTO_DATABASE = true;
	public static int NUM_GEN_THREADS = 4;
	
	//Required for RS_NN estimation
	public static final int RSNN_ESTIMATION_BUDGET = 100;
	public static final int RSNN_ESTIMATION_START_THRESHOLD = 1000;

//	END USER SETTABLE CONSTANTS 







	// For imposing restrictions on diagram generation.
	public static boolean LIMITED_DIAGRAMS = false;

	// Data Sizes
	public static int SMALL_COLUMN = 32;
	public static int MEDIUM_COLUMN = 64;
	public static int QTNAME_LENGTH = 128;
	public static int LARGE_COLUMN = 512;
	
	//operator or parameter level
	public static boolean OP_LVL=false;

	// Skew for the exponential distribution
	public static double QDIST_SKEW_10 = 2.0;
	public static double QDIST_SKEW_30 = 1.33;
	public static double QDIST_SKEW_100 = 1.083;
	public static double QDIST_SKEW_300 = 1.027;
	public static double QDIST_SKEW_1000 = 1.00808;

	// Enable AbstractPlan feature on compatible engines
	public static boolean  ENABLE_COST_MODEL = true;

	public static boolean IS_DEVELOPER = false;
	
	public static boolean IS_CLASS2_OPT_ENABLED = false;

	public static final int SAVE_BATCH_SIZE = 100000;
	
	// Menu Action
	public static final int NEW_DB_INSTANCE = 1;
	public static final int EDIT_DB_INSTANCE = 2;
	public static final int DELETE_DB_INSTANCE = 3;
	public static final int GET_DIAGRAM_LIST = 4;
	public static final int DELETE_DIAGRAM = 5;
	public static final int RENAME_DIAGRAM = 14;
	public static final int CONNECT_PICASSO = 12;
	public static final int CHECK_SERVER = 6;
	public static final int ABOUT_SERVER = 7;
	public static final int CLEAN_PROCESSES = 8;
	public static final int CLEAN_PICDB = 9;
	public static final int DELETE_PICDB = 10;
	public static final int SHUTDOWN_SERVER = 11;
	public static final int SHOW_PICASSO_SETTINGS = 13;



	// Query Points
	public static final double LOWER_DIAGONAL = 0.05;
	public static final double MID_DIAGONAL = 0.5;
	public static final double HIGHER_DIAGONAL = 0.95;

	public static final int HIGH_QNO = 30000000;

	public static final int	SEGMENT_SIZE = 9;
	public static final int STATUS_REFRESH_SIZE = 9; // changed for 9 to 100 so that the status message is sent every 100 queries instead of 10

	// Diagram Settings
	public static char DIAGRAM_REQUEST_TYPE = 'C';
	public static boolean IS_APPROXIMATE_DIAGRAM = false;
	public static final String COMPILETIME_DIAGRAM = "COMPILETIME";
	public static final String APPROX_COMPILETIME_DIAGRAM = "APPROX_COMPILETIME";	
	public static final String RUNTIME_DIAGRAM = "RUNTIME";
	public static final String UNIFORM_DISTRIBUTION = "UNIFORM";
	public static final String EXPONENTIAL_DISTRIBUTION = "EXPONENTIAL";
	public static final String OPERATORLEVEL = "OPERATOR";
	public static final String SUBOPERATORLEVEL = "SUB-OPERATOR";
	// The term "Sub-Operator" is replaced with "Parameter" in the documentation and in the interface

	public static final String INPUT_QUERY_FOLDER = "QueryTemplates";
	public static final String INPUT_IMAGE_FOLDER = "Images";
	//public static final String SETTINGS_FILE = "DBSettings";
	public static final String DB_SETTINGS_FILE = "PicassoRun/DBConnections";
	public static final String PICASSO_SETTINGS_FILE = "PicassoRun/local_conf";
	public static final String IMAGE_URL = "images/picassologo.jpg";
	public static final String MINI_LOGO = "images/mini_logo.gif";
	public static final String IISC_LOGO = "images/iisc_logo.jpg";
	public static final String ZOOM_IN_IMAGE = "images/zoomin.jpg";
	public static final String ZOOM_OUT_IMAGE = "images/zoomout.jpg";

	// Abstract Plan feature
	public static final String ABSTRACT_PLAN_COMMENT = "--Picasso_Abstract_Plan";
	public static final String SYBASE_ABSTRACT_PLAN_ENDS = "To experiment";

	// Plan Display
	public static final Color BUTTON_COLOR = Color.LIGHT_GRAY;
	public static final Color HIGHLIGHT_COLOR = Color.YELLOW;
	public static final Color IMAGE_BACKGROUND = Color.WHITE;
	public static final Color IMAGE_TEXT_COLOR = Color.BLUE;
	public static final Color PLAN_COLOR = new Color(0xff00de56);
	public static final Color EXEC_COLOR = Color.RED;

	public static final int matchColor[] = {
		0xff000000, 0xffff0000, 0xff0000ff, 0xff00ffff, 0xffff00ff,	0xffff5000, 0xff6000ff, 0xff70ffff, 0xffff80ff,
		0xffff0000, 0xff0000ff, 0xff00ffff, 0xffff00ff,	0xffff5000, 0xff6000ff, 0xff70ffff, 0xffff80ff
	};

	public static final int treeColor[] = {
		0xFFFFFF00,	0xFF33AA33, 0xFFFA83FA, 0xFFFFAFAF,	0xFFFFC800, 0xFFEC8807, 0xFF0088FF, 0xFF00FF00,
		0xFF009F3E, 0xFFAC99FF, 0xfffad300, 0xffa3dc5a,	0xEE117777, 0xFFDC8699, 0xFFF4C281, 0xffe0fa00,
		0xFF4567EE, 0xFFE395E3,	0xFF95E3E3, 0xFF688799,	0xfffa697f, 0xFF987654, 0xFF9872AA, 0xFFC3C3A1,
		0xFF4975BF, 0xFF67DE52, 0xFFDAF08B, 0xFF363A5B,	0xFFEFDFE0, 0xFF777733,	0xFF662222, 0xFF10FF01,
		0xFF7E6753, 0xFFCAF711, 0xFFA33803, 0xFFA59103,	0xFF6A6034, 0xFFF8DAFF, 0xFF222266, 0xFF337777,
		0xFF916BEC, 0xFFA3004A, 0xFFF5CEA0,	0xFF3cF1A4,	0xFFD3550E, 0xFF2300FF, 0xFFCF8677, 0xFFBBAA00,
		0xFF00AABB,	0xFFAA00BB, 0xFF66EE33, 0xFF555555,	0xFF669900, 0xFF6633EE, 0xFF33EE66, 0xFF654321,
		0xFF578577, 0xFF229900, 0xFFbfa3af, 0xFFad9ad1,	0xFF501020, 0xFFC010C0, 0xFF514122,	0xFFCCCCCC,
		0xFF5CFFC0, 0xFF2CF10E, 0xFF2FF2DF, 0xFFDFFF22,	0xFF843065, 0xFF475577, 0xFF677387,	0xFF998933,
		0xFF114441, 0xFF333444, 0xFF00478F, 0xFF0011FF,	0xFF56E12E, 0xFF2EE13E, 0xFF226622, 0xFF3E5612,
		0xFF119911,0xFF00FF00,0xFF977A5B,	
		/*0xFFFFFF00, 0xffa2fafa, 0xfffa83fa, 0xffffafaf, 0xffffc800, 0xfffa703e, 0xff7ffafa, 0xff00ff00, 0xff009f3e*/
	};

	public static final int color[]  = {
		0xFFFF0000,	0xFF0000FF, 0xFF991111, 0xffffff00,	0xffa654da, 0xFFEC8807, 0xFF0088FF, 0xFFF78789,
		0xFF33d8ed, 0xFFAC99FF, 0xFFFF1199, 0xffdb7f6c,	0xff74fad5, 0xFFAA33AA, 0xFFAA3333, 0xFFEE2345,
		0xff537bc3, 0xffe35be3,	0xff72dbd5, 0xFF123456,	0xFFAB2358, 0xFF987654, 0xFF9872AA, 0xFFC3C3A1,
		0xFF27529F, 0xFF67DE52, 0xFFDAF08B, 0xFF363A5B,	0xFFEFDFE0, 0xFF777733,	0xFF662222, 0xFF10FF01,
		0xFF7E6753, 0xFFCAF711, 0xFFA33803, 0xFFA59103,	0xFF6A6034, 0xFFF8DAFF, 0xFF222266, 0xFF337777,
		0xFF916BEC, 0xFFA3004A, 0xFFF5CEA0,	0xFF3cF1A4,	0xFFD3550E, 0xFF2300FF, 0xFFCF8677, 0xFFBBAA00,
		0xFF00AABB,	0xFFAA00BB, 0xFF66EE33, 0xFF555555,	0xFF669900, 0xFF6633EE, 0xFF33EE66, 0xFF654321,
		0xFF578577, 0xFF229900, 0xFF3A1223, 0xFF3A1287,	0xFF501020, 0xFFC010C0, 0xFF514122,	0xFFCCCCCC,
		0xFF5CFFC0, 0xFF2CF10E, 0xFF2FF2DF, 0xFFDFFF22,	0xFF843065, 0xFF475577, 0xFF677387,	0xFF998933,
		0xFF114441, 0xFF333444, 0xFF00478F, 0xFF0011FF,	0xFF56E12E, 0xFF2EE13E, 0xFF226622, 0xFF3E5612,
		0xFF119911, 0xFF00FF00, 0xFF977A5B, 0xFF000000   // The last color black has to be there..
	};


	public static final int DEFAULT_TREE_NODE_COLOR = 0xfff3faaa;


	public static final String SCALE_FONT = /*"Courier New";  "Microsoft Sans Serif";*/"Arial";
	public static final int FONT_SIZE = 22;
	public static final int ONE_D = 1;
	public static final int TWO_D = 2;
	public static final int THREE_D = 3;

	public static final int LEGEND_SIZE = 25;
	public static final int LEGEND_MARGIN_Y = 20;
	public static final int LEGEND_MARGIN_X = 2;
	public static final String LEGEND_FONT = "Arial";
	public static final int LEGEND_FONT_SIZE = 14;

	public static final double ASPECT_2D_X = 0.8;
	public static final double ASPECT_2D_Y = 0.8;
	public static final double ASPECT_X = 0.8;
	public static final double ASPECT_Y = 0.8;
	public static final double ASPECT_Z = 0.8;

	public static final int STATUS_LENGTH = 500;

	// Selectivities
	public static final int PICASSO_SELECTIVITY = 0;
	public static final int PREDICATE_SELECTIVITY = 1;
	public static final int PLAN_SELECTIVITY = 2;
	public static final int DATA_SELECTIVITY = 3;

	// Compiled Plan Tree
	public static final int SHOW_BOTH = 0;
	public static final int SHOW_COST = 1;
	public static final int SHOW_CARD = 2;
	public static final int SHOW_NONE = 3;

	// PlanDiff 
	public static final String IS_NODE_SIMILAR = "ISNDSM";
	public static final Color SAME_NODE_COLOR = Color.WHITE; // Temp

	// PlanDiff at OPERATOR Level
	public static final int T_IS_SIMILAR = 0;
	public static final int T_SUB_OP_DIF = 1;
	public static final int T_LEFT_EQ_RIGHT = 3;
	public static final int T_LEFT_SIMILAR = 4;
	public static final int T_RIGHT_SIMILAR = 5;
	public static final int T_NO_CHILD_SIMILAR = 8;
	public static final int T_NP_SIMILAR = 9;
	public static final int T_NP_LEFT_EQ_RIGHT = 10;
	public static final int T_NP_LEFT_SIMILAR = 11;
	public static final int T_NP_RIGHT_SIMILAR = 12;
	public static final int T_NP_NOT_SIMILAR = 15;
	public static final int T_NO_DIFF_DONE = 16;
	public static final int T_EDIT_NODE = 17;

	// The following constants are used only to set the two trees properly in PlanDiff
	public static final int T_NP_LR_SIMILAR = 13;
	public static final int T_NP_RL_SIMILAR = 14;
	public static final int T_LR_SIMILAR = 6;
	public static final int T_RL_SIMILAR = 7;

	public static final int T_NP_LEFT_EQ = 13;
	public static final int T_NP_RIGHT_EQ = 14;
	public static final int T_LEFT_EQ = 6;
	public static final int T_RIGHT_EQ = 7;

	// PlanDiff at SUBOPERATOR (aka PARAMETER) Level 
	public static final int SO_BASE = 20;
	public static final int T_SO_LEFT_EQ_RIGHT = SO_BASE + T_LEFT_EQ_RIGHT;
	public static final int T_SO_LEFT_SIMILAR = SO_BASE + T_LEFT_SIMILAR;
	public static final int T_SO_RIGHT_SIMILAR = SO_BASE + T_RIGHT_SIMILAR;
	public static final int T_SO_NO_CHILD_SIMILAR = SO_BASE + T_NO_CHILD_SIMILAR;
	public static final int T_SO_LR_SIMILAR = SO_BASE + T_LR_SIMILAR;
	public static final int T_SO_RL_SIMILAR = SO_BASE + T_RL_SIMILAR;
//Data for initialization of range frame
	public static int[] prevselected = {0,0,0,0,0};
	public static double[] slice={0,0,0,0,0};
	public static boolean first=true;
	public 	static String[]	params;
	// Data Types
	public static final Vector<String> INT_ALIASES;
	public static final Vector<String> REAL_ALIASES;
	public static final Vector<String> STRING_ALIASES;
	public static final Vector<String> DATE_ALIASES;
	
	
	public static final int TOTAL_PANE = 7;
	public static final int QUERY_PANE = 0;
	public static final int EMPTY_PANE1 = 1;
	public static final int NATIVE_PANE = 2; 
	public static final int EMPTY_PANE2 = 3;
	public static final int BOUQUET_IDNT_PANE = 4;
//	public static final int OPT_PANE = 5;
	public static final int BOUQUET_PANE = 5;
	public static final int RESULT_PANE = 6;
	
	
	
//	public static final int INPUT_PANEL_HEIGHT = 40;
//	public static int INPUT_PANEL_HEIGHT = 50;		//for demo
	public static int HEAD_PANEL_HEIGHT;
	public static int INPUT_PANEL_HEIGHT;
	
	
	public static final String TEXT_FONT = "Arial";
	public static final String COMPONENT_FONT = "Ubuntu";
	
	
	
	public static final boolean MAXIMISE_WINDOW = true;
	public static final int FRAME_WIDTH = 1920;
	public static final int FRAME_HEIGHT = 1080;
	
	public static double SCREEN_WIDTH;
	public static double SCREEN_HEIGHT;
	
//	public static final String TEXT_FONT = "Helvetica";
	
	public static int STATUS_PANEL_HEIGHT;
	public static int STATUS_PANEL_FONT_SIZE;		//for demo
//	public static final int STATUS_PANEL_FONT_SIZE = 16;
	
	public static final int HEADING_FONT_SIZE = 22;
	public static final int LARGE_FONT_SIZE = 20;
	public static final int MEDIUM_FONT_SIZE = 18;
	public static final int NORMAL_FONT_SIZE = 16;
	public static final int AXIS_TICK_FONT_SIZE = 15;
	
	public static final int AXIS_LABEL_FONT_SIZE = 16;
	public static final int AXIS_LABEL_SAMLL_FONT_SIZE = 11;
	
	public static final int BUTTON_FONT_SIZE = 18;
	
//	public static final int backgroundColor1 = 0xFF128DD5;
	public static final int backgroundColor1 = 0xFF5D7BBA;
//	public static final int backgroundColor1 = 0xFF4C66A4; 
	public static final int backgroundColor2 = 0xFFE7EFFF;
//	public static final int backgroundColor2 = 0xFFEEEFF4;
//	public static final int STATUS_PANEL_COLOR = 0xFFB9C6E4;
	public static final int STATUS_PANEL_COLOR = 0xFF5D7BBA;
	
	public static final int ABSTRACT_EXECUTION = 0;
	public static final int REAL_EXECUTION = 1;
	
	public static final int WITHOUT_USER_CONTROL = 0;
	public static final int WITH_USER_CONTROL = 1;

	static{
		INT_ALIASES=new Vector<String>();
		REAL_ALIASES=new Vector<String>();
		STRING_ALIASES=new Vector<String>();
		DATE_ALIASES=new Vector<String>();

		INT_ALIASES.add("INTEGER");
		INT_ALIASES.add("BIGINT");
		INT_ALIASES.add("SMALLINT");
		INT_ALIASES.add("INT");
		INT_ALIASES.add("TINYINT");
		INT_ALIASES.add("LONG");
		INT_ALIASES.add("INT2");
		INT_ALIASES.add("INT4");
		INT_ALIASES.add("INT8");

		REAL_ALIASES.add("REAL");
		REAL_ALIASES.add("DECIMAL");
		REAL_ALIASES.add("DOUBLE");
		REAL_ALIASES.add("FLOAT");
		REAL_ALIASES.add("NUMBER");
		REAL_ALIASES.add("NUMERIC");
		REAL_ALIASES.add("FLOAT4");
		REAL_ALIASES.add("FLOAT8");
		REAL_ALIASES.add("SMALLFLOAT");

		STRING_ALIASES.add("CHAR");
		STRING_ALIASES.add("VARCHAR");
		STRING_ALIASES.add("NVARCHAR");
		STRING_ALIASES.add("VARCHAR2");
		STRING_ALIASES.add("NCHAR");
		STRING_ALIASES.add("SYSNAME");
		STRING_ALIASES.add("NVARCHAR");
		STRING_ALIASES.add("LVARCHAR");
		
		DATE_ALIASES.add("DATE");
		DATE_ALIASES.add("DATETIME");
		DATE_ALIASES.add("SMALLDATETIME");
	}
}
