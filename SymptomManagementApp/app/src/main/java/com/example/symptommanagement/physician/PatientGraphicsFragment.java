package com.example.symptommanagement.physician;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import com.androidplot.Plot;
import com.androidplot.pie.PieChart;
import com.androidplot.pie.Segment;
import com.androidplot.pie.SegmentFormatter;
import com.androidplot.ui.AnchorPosition;
import com.androidplot.xy.*;
import com.example.symptommanagement.R;
import com.example.symptommanagement.data.MedicationLog;
import com.example.symptommanagement.data.PainLog;
import com.example.symptommanagement.data.Patient;
import com.example.symptommanagement.data.graphics.EatingPlotPoint;
import com.example.symptommanagement.data.graphics.MedicationPlotPoint;
import com.example.symptommanagement.data.graphics.SeverityPlotPoint;
import com.example.symptommanagement.data.graphics.TimePoint;
import com.example.symptommanagement.databinding.FragmentPatientGraphicsBinding;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * A fragment to display graphical data related to a patient's information, such as severity, eating habits, etc.
 * The fragment provides different types of charts to visualize the data.
 */
public class PatientGraphicsFragment extends Fragment {

    private final static String LOG_TAG = PatientGraphicsFragment.class.getSimpleName();
    public final static String FRAGMENT_TAG = "fragment_patient_graphics";
    private FragmentPatientGraphicsBinding binding;

    /**
     * Callback interface for obtaining patient data to be displayed in the graphs.
     */
    public interface Callbacks {
        /**
         * Retrieves the patient data for graphing.
         *
         * @return The Patient object containing the data for the graphs.
         */
        Patient getPatientDataForGraphing();
    }

    private static final long MS_IN_A_DAY = 86400000;

    /**
     * Enum to represent the different types of patient graphs available.
     * Each graph type has an associated numeric value.
     */
    public enum PatientGraph {
        NO_CHART(0), LINE_PLOT(100), FUZZY_CHART(200), SCATTER_CHART(300),
        PIE_CHART(400), BAR_CHART(500);

        private final int value;

        PatientGraph(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        /**
         * Finds the PatientGraph enum corresponding to the given numeric value.
         *
         * @param val The numeric value of the PatientGraph.
         * @return The PatientGraph enum associated with the given value, or NO_CHART if not found.
         */
        public static PatientGraph findByValue(int val) {
            for (PatientGraph s : values()) {
                if (s.getValue() == val) {
                    return s;
                }
            }
            return NO_CHART;
        }
    }

    /**
     * Patient object to store patient information for graphing
     */
    private Patient patient;

    /**
     * ID of the patient
     */
    private String patientId = null;

    /**
     * XYPlotZoomPan object to handle zooming and panning for the patient XY plot
     */
    private XYPlotZoomPan simplePatientXYPlot;

    /**
     * Series to plot eating data over time
     */
    private SimpleXYSeries eatingSeries = null;  // by time

    /**
     * Series to plot eating data aggregated by hour
     */
    private SimpleXYSeries eatingSeriesByHour = null;

    /**
     * Series to plot eating data aggregated by day
     */
    private SimpleXYSeries eatingSeriesByDay = null;

    /**
     * Count of data points where the patient is not eating
     */
    private int notEatingCount = 0;

    /**
     * Count of data points where the patient is eating some food
     */
    private int eatingSomeCount = 0;

    /**
     * Count of data points where the patient is eating OK
     */
    private int eatingOkCount = 0;

    /**
     * Series to plot severity data over time
     */
    private SimpleXYSeries severitySeries = null; // by time

    /**
     * Series to plot severity data aggregated by hour
     */
    private SimpleXYSeries severitySeriesByHour = null;

    /**
     * Series to plot severity data aggregated by day
     */
    private SimpleXYSeries severitySeriesByDay = null;

    /**
     * Count of data points with severe pain severity
     */
    private int severeCount = 0;

    /**
     * Count of data points with moderate pain severity
     */
    private int moderateCount = 0;

    /**
     * Count of data points with controlled pain severity
     */
    private int controlledCount = 0;

    /**
     * PieChart to display data in a pie chart
     */
    private PieChart pie;

    /**
     * SegmentFormatter for the first segment in the pie chart
     */
    private SegmentFormatter sf1;

    /**
     * SegmentFormatter for the second segment in the pie chart
     */
    private SegmentFormatter sf2;

    /**
     * SegmentFormatter for the third segment in the pie chart
     */
    private SegmentFormatter sf3;

    /**
     * Segments for severe, moderate, and controlled pain in the pie chart
     */
    private Segment painSevere;
    private Segment painModerate;
    private Segment painControlled;

    /**
     * Segments for eating OK, eating some, and not eating in the pie chart
     */
    private Segment eatingOK;
    private Segment eatingSome;
    private Segment eatingNone;

    /**
     * Enum to represent the current type of patient graph being displayed
     */
    private PatientGraph graph = PatientGraph.LINE_PLOT;

    /**
     * Lists to store plot points for severity, eating, and medication data
     */
    private List<SeverityPlotPoint> severityPoints = null;
    private List<EatingPlotPoint> eatingPoints = null;
    private List<MedicationPlotPoint> medicationPoints = null;

    /**
     * Layout to display the XY plot
     */
    private LinearLayout xyChartLayout;

    /**
     * Layout to display the pie chart
     */
    private LinearLayout pieChartLayout;

    /**
     * Boolean flags to control the visibility of eating and severity data in the graphs
     */
    private boolean showEating = true;
    private boolean showSeverity = true;

    /**
     * Called when the fragment is created. This method is used to initialize the fragment's state.
     *
     * @param savedInstanceState A Bundle containing the saved state of the fragment, if available.
     *                           Can be null if the fragment is newly created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Call the superclass's onCreate method to perform necessary setup
        super.onCreate(savedInstanceState);
        // Retain the instance of the fragment across configuration changes
        setRetainInstance(true);
    }

    /**
     * Called when the activity's onCreate() method has completed. This is a good place to
     * perform any additional setup or initialization after the activity has been created.
     *
     * @param savedInstanceState A Bundle containing the saved state of the fragment, if available.
     *                           Can be null if the fragment is newly created.
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // Call the superclass's onActivityCreated method to complete the setup
        super.onActivityCreated(savedInstanceState);
        // Retain the instance of the fragment across configuration changes
        this.setRetainInstance(true);
    }

    /**
     * Called when the fragment is attached to an activity. This method is used to verify that
     * the hosting activity implements the required Callbacks interface.
     *
     * @param activity The activity to which the fragment is attached.
     * @throws IllegalStateException if the hosting activity does not implement Callbacks interface.
     */
    @Override
    public void onAttach(Activity activity) {
        // Call the superclass's onAttach method to perform necessary setup
        super.onAttach(activity);
        // Check if the hosting activity implements the Callbacks interface
        if (!(activity instanceof Callbacks)) {
            // Throw an IllegalStateException if the hosting activity does not implement Callbacks
            throw new IllegalStateException(activity.getString(R.string.callbacks_message));
        }
    }

    /**
     * Called when the fragment's view is created. This method is responsible for inflating the fragment's layout
     * and initializing its UI components.
     *
     * @param inflater           The LayoutInflater used to inflate the fragment's layout.
     * @param container          The parent ViewGroup in which the fragment's UI will be placed.
     * @param savedInstanceState A Bundle containing the saved state of the fragment's UI, if available.
     *                           Can be null if the fragment is newly created.
     * @return The root View of the fragment's layout.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the fragment's layout using data binding
        binding = FragmentPatientGraphicsBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();
        // Get references to the XY plot layout and Pie chart layout
        xyChartLayout = rootView.findViewById(R.id.xy_chart_layout);
        simplePatientXYPlot = rootView.findViewById(R.id.patientGraphicsPlot);
        commonAndroidPlotSetting();
        xyChartLayout.setVisibility(View.INVISIBLE); // Set XY plot layout initially invisible
        pieChartLayout = rootView.findViewById(R.id.pie_chart_layout);
        pie = rootView.findViewById(R.id.PatientPieChart); // Get reference to the PieChart view
        return rootView; // Return the root View of the fragment's layout
    }

    /**
     * Called when the view hierarchy is created. This method is used to perform additional setup
     * after the view hierarchy is created and visible to the user.
     *
     * @param view               The View created by onCreateView().
     * @param savedInstanceState A Bundle containing the saved state of the fragment's UI, if available.
     *                           Can be null if the fragment is newly created.
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Call the superclass's onViewCreated method to complete the setup
        super.onViewCreated(view, savedInstanceState);
        // Restart the graph and setup click listeners for different graph options
        restartGraph();
        binding.linePlot.setOnClickListener(v -> onClickLinePlot());
        binding.barPlot.setOnClickListener(v -> onClickBarPlot());
        binding.piePlot.setOnClickListener(v -> onClickPiePlot());
        binding.scatterPlot.setOnClickListener(v -> onClickScatterPlot());
        binding.pieChartSeverity.setOnClickListener(this::onPieChartGroup);
        binding.pieChartEating.setOnClickListener(this::onPieChartGroup);
        binding.graphChoice1.setOnClickListener(this::onCheckboxGroup);
        binding.graphChoice2.setOnClickListener(this::onCheckboxGroup);
    }

    /**
     * Called when the fragment is visible to the user and actively running. This method is used to
     * restart the graph and update the patient data for graphing when the fragment resumes.
     */
    @Override
    public void onResume() {
        // Call the superclass's onResume method to perform necessary setup
        super.onResume();
        // Restart the graph to update with the latest data
        restartGraph();
    }

    /**
     * Restarts the graph based on the selected graph type (e.g., line plot, bar chart, etc.).
     * The visibility of the XY chart layout is updated accordingly, and the corresponding graph creation method is called.
     * If the selected graph type is invalid, the XY chart layout is set to invisible, and an error message is logged.
     */
    private void restartGraph() {
        Log.d(LOG_TAG, "RESTARTING GRAPH ==> Type: " + graph.toString());
        xyChartLayout.setVisibility(View.VISIBLE);

        switch (graph) {
            case LINE_PLOT:
                onClickLinePlot();
                break;
            case BAR_CHART:
            case FUZZY_CHART:
                onClickBarPlot();
                break;
            case SCATTER_CHART:
                onClickScatterPlot();
                break;
            case PIE_CHART:
                onClickPiePlot();
                break;
            default:
                xyChartLayout.setVisibility(View.INVISIBLE);
                Log.e(LOG_TAG, "Invalid chart type on restart.");
                break;
        }
    }

    /**
     * Handles the click event for the "Line Plot" button.
     * If the patient data is ready for graphing, sets the graph type to line plot and creates the line plot.
     */
    public void onClickLinePlot() {
        Log.d(LOG_TAG, "Line Plot Clicked");
        if (patientReadyForGraphing()) {
            graph = PatientGraph.LINE_PLOT;
            createLinePlot();
        }
    }

    /**
     * Handles the click event for the "Bar Chart" button.
     * If the patient data is ready for graphing, sets the graph type to bar chart and creates the bar chart.
     */
    public void onClickBarPlot() {
        Log.d(LOG_TAG, "Bar Chart Clicked");
        if (patientReadyForGraphing()) {
            graph = PatientGraph.BAR_CHART;
            createBarChart();
        }
    }

    /**
     * Handles the click event for the "Scatter Plot" button.
     * If the patient data is ready for graphing, sets the graph type to scatter plot and creates the scatter plot.
     */
    public void onClickScatterPlot() {
        Log.d(LOG_TAG, "Scatter Plot Clicked");
        if (patientReadyForGraphing()) {
            graph = PatientGraph.SCATTER_CHART;
            createScatterPlot();
        }
    }

    /**
     * Handles the click event for the "Pie Chart" button.
     * If the patient data is ready for graphing, sets the graph type to pie chart and creates the pie chart.
     */
    public void onClickPiePlot() {
        Log.d(LOG_TAG, "Pie Plot Clicked");
        if (patientReadyForGraphing()) {
            graph = PatientGraph.PIE_CHART;
            createPieChart();
        }
    }

    /**
     * Checks if the patient data is ready for graphing. If not, retrieves the patient data from the hosting activity
     * and generates data lists for graphing based on the patient's pain logs and medication logs.
     *
     * @return true if the patient data is ready for graphing, false otherwise.
     */
    private boolean patientReadyForGraphing() {
        if (patient == null || patientId == null) {
            patient = ((Callbacks) getActivity()).getPatientDataForGraphing();

            if (patient == null || patient.getId() == null) {
                Log.d(LOG_TAG, "NO Patient Data for Graphing!");
                return false;
            }
            if (patientId == null) {
                patientId = patient.getId();
            }
            if (!patient.getId().contentEquals(patientId) ||
                    severityPoints == null || eatingPoints == null || medicationPoints == null) {
                Log.d(LOG_TAG, "This is a new patient so we need to recalculate the series.");
                generatePatientDataLists(patient);
            }
            Log.d(LOG_TAG, "Current Patient to be Graphed : " + patient);
        }
        return true;
    }

    /**
     * Updates the patient data for graphing when a new patient arrives.
     *
     * @param patient The new patient data to be graphed.
     */
    public void updatePatient(Patient patient) {
        if (patient == null) {
            Log.e(LOG_TAG, "Trying to set graphing patient to null.");
            return;
        }
        Log.d(LOG_TAG, "New Patient has arrived!" + patient);
        this.patient = patient;
        patientId = patient.getId();
        generatePatientDataLists(this.patient);
        restartGraph();
    }

    /**
     * Generates data lists for graphing based on the patient's pain logs and medication logs.
     *
     * @param patient The patient data for graphing.
     */
    private void generatePatientDataLists(Patient patient) {
        if (patient == null) return;
        resetCounts();
        Collection<PainLog> painLogs = patient.getPainLog();

        if (painLogs != null && painLogs.size() > 0) {
            severityPoints = new ArrayList<>();
            eatingPoints = new ArrayList<>();
            for (PainLog log : painLogs) {
                Log.d(LOG_TAG, "Adding Pain Log to Graph Data " + log.toString());
                severityPoints.add(new SeverityPlotPoint(log.getCreated(), log.getSeverity().getValue()));
                updateSeverityCounts(log.getSeverity().getValue());
                eatingPoints.add((new EatingPlotPoint(log.getCreated(), log.getEating().getValue())));
                updateEatingCounts(log.getEating().getValue());

            }
        }
        Collection<MedicationLog> medicationLogs = patient.getMedLog();
        if (medicationLogs != null && medicationLogs.size() > 0) {
            medicationPoints = new ArrayList<>();
            for (MedicationLog m : medicationLogs) {
                Log.d(LOG_TAG, "Adding Medication Log to Graph Data " + m.toString());
                medicationPoints.add(new MedicationPlotPoint(m.getTaken(),
                        m.getMed().getId(), m.getMed().getName()));
            }
        }
        if (severityPoints != null && eatingPoints != null && medicationPoints != null) {
            Log.d(LOG_TAG, "Severity Points : " + severityPoints);
            Log.d(LOG_TAG, "Eating Points : " + eatingPoints.toString());
            Log.d(LOG_TAG, "Med Log Points : " + medicationPoints.toString());
        }
    }

    /**
     * This method applies common AndroidPlot settings to the 'simplePatientXYPlot'.
     * It customizes various visual aspects of the plot, such as colors, grid lines, labels, legend, etc.
     */
    private void commonAndroidPlotSetting() {
        // Set the plot border style to NONE and background color to white.
        simplePatientXYPlot.setBorderStyle(Plot.BorderStyle.NONE, null, null);
        simplePatientXYPlot.getGraphWidget().getBackgroundPaint().setColor(Color.WHITE);

        // Set the grid background color to transparent.
        simplePatientXYPlot.getGraphWidget().getGridBackgroundPaint().setColor(Color.TRANSPARENT);

        // Set the color and style of domain grid lines (x-axis).
        simplePatientXYPlot.getGraphWidget().getDomainGridLinePaint().setColor(Color.BLACK);
        simplePatientXYPlot.getGraphWidget().getDomainGridLinePaint().setPathEffect(new DashPathEffect(new float[]{1, 1}, 1));

        // Set the color and style of range grid lines (y-axis).
        simplePatientXYPlot.getGraphWidget().getRangeGridLinePaint().setColor(Color.BLACK);
        simplePatientXYPlot.getGraphWidget().getRangeGridLinePaint().setPathEffect(new DashPathEffect(new float[]{1, 1}, 1));

        // Set the color of domain and range origin lines.
        simplePatientXYPlot.getGraphWidget().getDomainOriginLinePaint().setColor(Color.BLACK);
        simplePatientXYPlot.getGraphWidget().getRangeOriginLinePaint().setColor(Color.BLACK);

        // Set grid padding and margins for the plot.
        simplePatientXYPlot.getGraphWidget().setMargins(dp2px(100), dp2px(20), dp2px(20), dp2px(50));
        simplePatientXYPlot.getGraphWidget().setGridPadding(30, 10, 30, 0);

        // Set text size for domain and range labels and origin labels.
        simplePatientXYPlot.getGraphWidget().getDomainLabelPaint().setTextSize(dp2px(10));
        simplePatientXYPlot.getGraphWidget().getDomainOriginLabelPaint().setTextSize(dp2px(10));
        simplePatientXYPlot.getGraphWidget().getRangeLabelPaint().setTextSize(dp2px(10));
        simplePatientXYPlot.getGraphWidget().getRangeOriginLabelPaint().setTextSize(dp2px(10));

        // Customize the legend's appearance.
        simplePatientXYPlot.getLegendWidget().getTextPaint().setColor(Color.DKGRAY);
        simplePatientXYPlot.getLegendWidget().getHeightMetric().setValue(dp2px(25));
        simplePatientXYPlot.getLegendWidget().getIconSizeMetrics().getHeightMetric().setValue(dp2px(13));
        simplePatientXYPlot.getLegendWidget().getIconSizeMetrics().getWidthMetric().setValue(dp2px(13));
        simplePatientXYPlot.getLegendWidget().getPositionMetrics().setAnchor(AnchorPosition.RIGHT_BOTTOM);
        simplePatientXYPlot.getLegendWidget().getPositionMetrics().getXPositionMetric().setValue(-150);
        simplePatientXYPlot.getLegendWidget().getTextPaint().setTextSize(dp2px(15));
        simplePatientXYPlot.getLegendWidget().getWidthMetric().setValue(1);

        // Set text size for range and domain label widgets.
        simplePatientXYPlot.getRangeLabelWidget().getLabelPaint().setTextSize(dp2px(10));
        simplePatientXYPlot.getDomainLabelWidget().getLabelPaint().setTextSize(dp2px(10));

        // Set an empty title for the plot.
        simplePatientXYPlot.setTitle("");

        // Set the text size for the title widget.
        simplePatientXYPlot.getTitleWidget().getLabelPaint().setTextSize(dp2px(16));
    }

    /**
     * A utility method to convert density-independent pixels (dp) to pixels (px).
     *
     * @param dp The value in dp to be converted to px.
     * @return The converted value in pixels (px).
     */
    public float dp2px(float dp) {
        Resources resources = this.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }


    /**
     * Creates a bar chart to visualize the patient's severity and eating data.
     */
    private void createBarChart() {
        setLayout(PatientGraph.BAR_CHART); // Set the layout for the bar chart

        int count = 0;
        if (severityPoints != null) {
            count++;
        }
        if (eatingPoints != null) {
            count++;
        }
        if (count == 0) {
            // If there is no data, log a message, and redraw the plot
            Log.d(LOG_TAG, "NO DATA to work with on the graphing!");
            simplePatientXYPlot.redraw();
            return;
        }

        long minDate = -1;
        long maxDate = minDate;
        Log.d(LOG_TAG, "Creating data to plot");

        // Check for severity data and create corresponding data series
        if (severityPoints != null) {
            severitySeries = new SimpleXYSeries("Severity Level");
            severityPoints.sort(new TimePointSorter());
            for (SeverityPlotPoint s : severityPoints) {
                if (minDate < 0) {
                    minDate = s.getActualDate();
                }
                if (maxDate < s.getActualDate()) {
                    maxDate = s.getActualDate();
                }
                severitySeries.addLast(s.getTimeValue(), s.getSeverityValue());
            }
        }
        Log.d(LOG_TAG, "Min Date: " + minDate + " Max Date: " + maxDate + " num days is " +
                (maxDate - minDate) / MS_IN_A_DAY + 1L);

        // Check for eating data and create corresponding data series
        if (eatingPoints != null) {
            eatingSeries = new SimpleXYSeries("Eating Ability");
            eatingPoints.sort(new TimePointSorter());
            for (EatingPlotPoint eat : eatingPoints) {
                eatingSeries.addLast(eat.getTimeValue(), eat.getEatingValue());
            }
        }

        simplePatientXYPlot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 100);
        simplePatientXYPlot.setRangeBoundaries(0, 300, BoundaryMode.FIXED);
        simplePatientXYPlot.getGraphWidget().setTicksPerRangeLabel(1);
        simplePatientXYPlot.getGraphWidget().getRangeLabelPaint().setColor(Color.BLACK);

        // Set the range value format for the plot based on severity and eating data
        simplePatientXYPlot.getGraphWidget().setRangeValueFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, @NonNull StringBuffer toAppendTo, @NonNull FieldPosition pos) {
                Number num = (Number) obj;
                switch (num.intValue()) {
                    case 100:
                        if (showSeverity && showEating) {
                            toAppendTo.append("Well-Controlled/Eating");
                        } else if (showSeverity) {
                            toAppendTo.append("Well-Controlled");
                        } else if (showEating) {
                            toAppendTo.append("Eating");
                        }
                        break;
                    case 200:
                        if (showSeverity && showEating) {
                            toAppendTo.append("Moderate/Eating Some");
                        } else if (showSeverity) {
                            toAppendTo.append("Moderate");
                        } else if (showEating) {
                            toAppendTo.append("Eating Some");
                        }
                        break;
                    case 300:
                        if (showSeverity && showEating) {
                            toAppendTo.append("Severe/Not Eating");
                        } else if (showSeverity) {
                            toAppendTo.append("Severe");
                        } else if (showEating) {
                            toAppendTo.append("Not Eating");
                        }
                        break;
                    default:
                        break;
                }
                return toAppendTo;
            }

            @Override
            public Object parseObject(String source, @NonNull ParsePosition pos) {
                return null;
            }
        });

        // Set domain properties and formatting
        simplePatientXYPlot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, MS_IN_A_DAY);
        simplePatientXYPlot.setDomainBoundaries(minDate, maxDate, BoundaryMode.FIXED);
        simplePatientXYPlot.getGraphWidget().setTicksPerDomainLabel(2);
        simplePatientXYPlot.setDomainLabel("");
        simplePatientXYPlot.getGraphWidget().setDomainLabelOrientation(-45);
        simplePatientXYPlot.getGraphWidget().getDomainLabelPaint().setColor(Color.BLACK);
        simplePatientXYPlot.getGraphWidget().setDomainValueFormat(new Format() {
            private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd");

            @Override
            public StringBuffer format(Object obj, @NonNull StringBuffer toAppendTo, @NonNull FieldPosition pos) {
                long timestamp = ((Number) obj).longValue();
                Date date = new Date(timestamp);
                return dateFormat.format(date, toAppendTo, pos);
            }

            @Override
            public Object parseObject(String source, @NonNull ParsePosition pos) {
                return null;
            }
        });

        // Set plot appearance properties
        commonAndroidPlotSetting();

        // Add severity series to the plot if it has data and is set to be shown
        if (severitySeries.size() > 0 && showSeverity) {
            BarFormatter bf1 = new BarFormatter(getResources().getColor(R.color.sm_severity), Color.TRANSPARENT);
            bf1.getFillPaint().setAlpha(200);
            simplePatientXYPlot.addSeries(severitySeries, bf1);
        }

        // Add eating series to the plot if it has data and is set to be shown
        if (eatingSeries.size() > 0 && showEating) {
            BarFormatter bf1 = new BarFormatter(getResources().getColor(R.color.sm_eating), Color.TRANSPARENT);
            bf1.getFillPaint().setAlpha(200);
            simplePatientXYPlot.addSeries(eatingSeries, bf1);
        }

        Log.d(LOG_TAG, "Redrawing the bar chart");

        // Set bar chart rendering style and redraw the plot
        BarRenderer renderer = (BarRenderer) simplePatientXYPlot.getRenderer(BarRenderer.class);
        if (renderer != null) {
            renderer.setBarRenderStyle(BarRenderer.BarRenderStyle.SIDE_BY_SIDE);
            renderer.setBarWidthStyle(BarRenderer.BarWidthStyle.FIXED_WIDTH);
            renderer.setBarWidth(30);
            renderer.setBarGap(10);
        }
        simplePatientXYPlot.redraw();
    }

    /**
     * Creates a line plot to visualize the patient's severity and eating data as 3-day averages.
     */
    private void createLinePlot() {
        // Set the layout for the line plot
        setLayout(PatientGraph.LINE_PLOT);

        int count = 0;
        // Check if there is severity data
        if (severityPoints != null) {
            count++;
        }
        // Check if there is eating data
        if (eatingPoints != null) {
            count++;
        }
        if (count == 0) {
            // If there is no data, log a message, and return without plotting
            Log.d(LOG_TAG, "NO DATA to work with on the graphing!");
            return;
        }

        long minDate = -1;
        long maxDate = minDate;
        Log.d(LOG_TAG, "Creating data to plot");

        // Check for severity data and create corresponding data series as 3-day averages
        if (severityPoints != null) {
            severitySeries = new SimpleXYSeries("Severity Level(3-day Avg)");
            severityPoints.sort(new TimePointSorter());
            SeverityPlotPoint[] sevArray =
                    severityPoints.toArray(new SeverityPlotPoint[0]);
            for (int i = 1; i < (severityPoints.size() - 1); i++) {
                double movingAverage = (sevArray[i - 1].getSeverityValue()
                        + sevArray[i].getSeverityValue()
                        + sevArray[i + 1].getSeverityValue()) / 3.0;
                if (minDate < 0) {
                    minDate = sevArray[i].getActualDate();
                }
                if (maxDate < sevArray[i].getActualDate()) {
                    maxDate = sevArray[i].getActualDate();
                }
                severitySeries.addLast(sevArray[i].getTimeValue(), movingAverage);
            }
        }
        Log.d(LOG_TAG, "Min Date: " + minDate + " Max Date: " + maxDate + " num days is " +
                (maxDate - minDate) / MS_IN_A_DAY + 1L);

        // Check for eating data and create corresponding data series as 3-day averages
        if (eatingPoints != null) {
            eatingSeries = new SimpleXYSeries("Eating Ability(3-day Avg)");
            eatingPoints.sort(new TimePointSorter());
            EatingPlotPoint[] eatArray =
                    eatingPoints.toArray(new EatingPlotPoint[0]);
            for (int i = 1; i < (eatingPoints.size() - 1); i++) {
                long value1 = (eatArray[i - 1].getEatingValue() == 100) ? 100 :
                        (eatArray[i - 1].getEatingValue() == 200) ? 150 : 200;
                long value2 = (eatArray[i].getEatingValue() == 100) ? 100 :
                        (eatArray[i].getEatingValue() == 200) ? 150 : 200;
                long value3 = (eatArray[i + 1].getEatingValue() == 100) ? 100 :
                        (eatArray[i + 1].getEatingValue() == 200) ? 150 : 200;
                double movingAverage = (value1 + value2 + value3) / 3.0;
                eatingSeries.addLast(eatArray[i].getTimeValue(), movingAverage);
            }
        }

        simplePatientXYPlot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 50);
        simplePatientXYPlot.setRangeBoundaries(100, 300, BoundaryMode.FIXED);
        simplePatientXYPlot.getGraphWidget().setTicksPerRangeLabel(1); // Label every tick
        simplePatientXYPlot.getGraphWidget().getRangeLabelPaint().setColor(Color.BLACK);

        // Set the range value format for the plot based on severity and eating data
        simplePatientXYPlot.getGraphWidget().setRangeValueFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, @NonNull StringBuffer toAppendTo, @NonNull FieldPosition pos) {
                Number num = (Number) obj;
                switch (num.intValue()) {
                    case 100:
                        if (showSeverity && showEating) {
                            toAppendTo.append("Well-Controlled/Eating");
                        } else if (showSeverity) {
                            toAppendTo.append("Well-Controlled");
                        } else if (showEating) {
                            toAppendTo.append("Eating");
                        }
                        break;
                    case 150:
                        if (showEating) {
                            toAppendTo.append("Eating Some");
                        }
                        break;
                    case 200:
                        if (showSeverity && showEating) {
                            toAppendTo.append("Moderate/Not Eating");
                        } else if (showSeverity) {
                            toAppendTo.append("Moderate");
                        } else if (showEating) {
                            toAppendTo.append("Not Eating");
                        }
                        break;
                    case 300:
                        if (showSeverity) {
                            toAppendTo.append("Severe");
                        }
                        break;
                    default:
                        break;
                }
                return toAppendTo;
            }

            @Override
            public Object parseObject(String source, @NonNull ParsePosition pos) {
                return null;
            }
        });

        simplePatientXYPlot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, MS_IN_A_DAY);
        simplePatientXYPlot.setDomainBoundaries(minDate, maxDate, BoundaryMode.FIXED);
        simplePatientXYPlot.getGraphWidget().setTicksPerDomainLabel(2);
        simplePatientXYPlot.setDomainLabel("");
        simplePatientXYPlot.getGraphWidget().setDomainLabelOrientation(-45);
        simplePatientXYPlot.getGraphWidget().getDomainLabelPaint().setColor(Color.BLACK);
        simplePatientXYPlot.getGraphWidget().setDomainValueFormat(new Format() {
            private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd");

            @Override
            public StringBuffer format(Object obj, @NonNull StringBuffer toAppendTo, @NonNull FieldPosition pos) {
                long timestamp = ((Number) obj).longValue();
                Date date = new Date(timestamp);
                return dateFormat.format(date, toAppendTo, pos);
            }

            @Override
            public Object parseObject(String source, @NonNull ParsePosition pos) {
                return null;
            }
        });

        commonAndroidPlotSetting();

        // Add severity series to the plot if it has data and is set to be shown
        if (severitySeries.size() > 0 && showSeverity) {
            LineAndPointFormatter severityLineFormat = new LineAndPointFormatter(
                    getResources().getColor(R.color.sm_severity), null,
                    getResources().getColor(R.color.sm_severity), null);
            severityLineFormat.getFillPaint().setAlpha(200);
            simplePatientXYPlot.addSeries(severitySeries, severityLineFormat);
        }

        // Add eating series to the plot if it has data and is set to be shown
        if (eatingSeries.size() > 0 && showEating) {
            LineAndPointFormatter eatingLineFormat = new LineAndPointFormatter(
                    getResources().getColor(R.color.sm_eating), null,
                    getResources().getColor(R.color.sm_eating), null);
            eatingLineFormat.getFillPaint().setAlpha(200);
            simplePatientXYPlot.addSeries(eatingSeries, eatingLineFormat);
        }

        Log.d(LOG_TAG, "Redrawing the Graph");
        simplePatientXYPlot.redraw(); // Redraw the line plot
    }

    /**
     * Creates a scatter plot to visualize the patient's severity and eating data by hour and day of the week.
     */
    private void createScatterPlot() {
        // Set the layout for the scatter plot
        setLayout(PatientGraph.SCATTER_CHART);

        int count = 0;
        // Check if there is severity data
        if (severityPoints != null) {
            count++;
        }
        // Check if there is eating data
        if (eatingPoints != null) {
            count++;
        }
        if (count == 0) {
            // If there is no data, log a message, redraw the plot, and return
            Log.d(LOG_TAG, "NO DATA to work with on the graphing!");
            simplePatientXYPlot.redraw();
            return;
        }

        Log.d(LOG_TAG, "Creating data to plot");

        // Check for severity data and create corresponding data series by hour and day of the week
        if (severityPoints != null) {
            severitySeriesByHour = new SimpleXYSeries("Severity By Hour");
            severitySeriesByDay = new SimpleXYSeries("Severity by Day of Week");
            for (SeverityPlotPoint s : severityPoints) {
                double valueHour = s.getHour() + (s.getMinutes() / 60.0);
                severitySeriesByHour.addLast(valueHour, s.getSeverityValue());
                double valueDay = s.getDayOfWeek() + (s.getHour() / 24.0);
                severitySeriesByDay.addLast(valueDay, s.getSeverityValue());
            }
        }

        // Check for eating data and create corresponding data series by hour and day of the week
        if (eatingPoints != null) {
            eatingSeriesByHour = new SimpleXYSeries("Eating Ability By Hour");
            eatingSeriesByDay = new SimpleXYSeries("Eating Ability By Day of Week");
            for (EatingPlotPoint eat : eatingPoints) {
                double valueHour = eat.getHour() + (eat.getMinutes() / 60.0);
                eatingSeriesByHour.addLast(valueHour, eat.getEatingValue());
                double valueDay = eat.getDayOfWeek() + (eat.getHour() / 24.0);
                eatingSeriesByDay.addLast(valueDay, eat.getEatingValue());
            }
        }

        simplePatientXYPlot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 1);
        simplePatientXYPlot.setDomainBoundaries(0, 23, BoundaryMode.FIXED);
        simplePatientXYPlot.getGraphWidget().setTicksPerDomainLabel(2);
        simplePatientXYPlot.setDomainLabel("");
        simplePatientXYPlot.getGraphWidget().setDomainLabelOrientation(-45);
        simplePatientXYPlot.getGraphWidget().getDomainLabelPaint().setColor(Color.BLACK);

        // Set the domain value format for the plot to display hours and AM/PM
        simplePatientXYPlot.getGraphWidget().setDomainValueFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, @NonNull StringBuffer toAppendTo, @NonNull FieldPosition pos) {
                int time = ((Number) obj).intValue();
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, time);
                cal.set(Calendar.MINUTE, 0);
                int hour = cal.get(Calendar.HOUR);
                if (hour == 0) {
                    hour = 12;
                }
                int am_pm = cal.get(Calendar.AM_PM);
                String display = hour + ":00" + (am_pm == 1 ? "PM" : "AM");
                return toAppendTo.append(display);
            }

            @Override
            public Object parseObject(String source, @NonNull ParsePosition pos) {
                return null;
            }
        });

        simplePatientXYPlot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 50);
        simplePatientXYPlot.setRangeBoundaries(50, 350, BoundaryMode.FIXED);
        simplePatientXYPlot.getGraphWidget().setTicksPerRangeLabel(1);
        simplePatientXYPlot.getGraphWidget().getRangeLabelPaint().setColor(Color.BLACK);

        // Set the range value format for the plot based on severity and eating data
        simplePatientXYPlot.getGraphWidget().setRangeValueFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, @NonNull StringBuffer toAppendTo, @NonNull FieldPosition pos) {
                Number num = (Number) obj;
                switch (num.intValue()) {
                    case 100:
                        if (showSeverity && showEating) {
                            toAppendTo.append("Well-Controlled/Eating");
                        } else if (showSeverity) {
                            toAppendTo.append("Well-Controlled");
                        } else if (showEating) {
                            toAppendTo.append("Eating");
                        }
                        break;
                    case 200:
                        if (showSeverity && showEating) {
                            toAppendTo.append("Moderate/Eating Some");
                        } else if (showSeverity) {
                            toAppendTo.append("Moderate");
                        } else if (showEating) {
                            toAppendTo.append("Eating Some");
                        }
                        break;
                    case 300:
                        if (showSeverity && showEating) {
                            toAppendTo.append("Severe/Not Eating");
                        } else if (showSeverity) {
                            toAppendTo.append("Severe");
                        } else if (showEating) {
                            toAppendTo.append("Not Eating");
                        }
                        break;
                    default:
                        break;
                }
                return toAppendTo;
            }

            @Override
            public Object parseObject(String source, @NonNull ParsePosition pos) {
                return null;
            }
        });

        commonAndroidPlotSetting();

        Log.d(LOG_TAG, "setting up the drawing information");

        // Add severity series by hour to the plot if it has data and is set to be shown
        if (severitySeriesByHour.size() > 0 && showSeverity) {
            LineAndPointFormatter severityPointFormat = new LineAndPointFormatter(
                    null, getResources().getColor(R.color.sm_severity), null, null);
            simplePatientXYPlot.addSeries(severitySeriesByHour, severityPointFormat);
        }

        // Add eating series by hour to the plot if it has data and is set to be shown
        if (eatingSeriesByHour.size() > 0 && showEating) {
            LineAndPointFormatter eatingPointFormat = new LineAndPointFormatter(
                    null, getResources().getColor(R.color.sm_eating), null, null);
            simplePatientXYPlot.addSeries(eatingSeriesByHour, eatingPointFormat);
        }

        Log.d(LOG_TAG, "Redrawing the Graph");
        // Redraw the scatter plot
        simplePatientXYPlot.redraw();
    }

    /**
     * Creates a pie chart to visualize the patient's severity and eating data.
     */
    private void createPieChart() {
        setLayout(PatientGraph.PIE_CHART); // Set the layout for the pie chart

        // Create segments for each category of severity and eating
        painSevere = new Segment("Severe", severeCount);
        painModerate = new Segment("Moderate", moderateCount);
        painControlled = new Segment("Well-Controlled", controlledCount);

        eatingNone = new Segment("Not Eating", notEatingCount);
        eatingSome = new Segment("Some", eatingSomeCount);
        eatingOK = new Segment("Eating OK", eatingOkCount);

        // Create segment formatters and configure them with colors and styles
        sf1 = new SegmentFormatter();
        sf1.getFillPaint().setAlpha(200);
        sf1.getLabelPaint().setColor(Color.BLACK);
        sf1.configure(getActivity(), R.xml.pie_segment_formatter1);

        sf2 = new SegmentFormatter();
        sf2.getFillPaint().setAlpha(200);
        sf2.getLabelPaint().setColor(Color.BLACK);
        sf2.configure(getActivity(), R.xml.pie_segment_formatter2);

        sf3 = new SegmentFormatter();
        sf3.getFillPaint().setAlpha(200);
        sf3.getLabelPaint().setColor(Color.BLACK);
        sf3.configure(getActivity(), R.xml.pie_segment_formatter3);

        // Set up the initial view based on the selected graph type (severity or eating)
        onPieChartGroup(showSeverity ? binding.pieChartSeverity : binding.pieChartEating);
        binding.pieChartRadioGroup.check(showSeverity ? R.id.pie_chart_severity : R.id.pie_chart_eating);

        pie.getBorderPaint().setColor(Color.WHITE);
        pie.getBackgroundPaint().setColor(Color.WHITE);
    }

    /**
     * Switches between showing the severity or eating data on the pie chart.
     *
     * @param v The view representing the selected graph type (severity or eating).
     */
    public void onPieChartGroup(View v) {
        switch (v.getId()) {
            case R.id.pie_chart_severity:
                showSeverity = true;
                pie.removeSeries(eatingOK);
                pie.removeSeries(eatingSome);
                pie.removeSeries(eatingNone);
                pie.addSeries(painSevere, sf1);
                pie.addSeries(painModerate, sf2);
                pie.addSeries(painControlled, sf3);
                // Redraw the pie chart
                pie.redraw();
                break;
            case R.id.pie_chart_eating:
                showSeverity = false;
                pie.removeSeries(painSevere);
                pie.removeSeries(painModerate);
                pie.removeSeries(painControlled);
                pie.addSeries(eatingNone, sf1);
                pie.addSeries(eatingSome, sf2);
                pie.addSeries(eatingOK, sf3);
                // Redraw the pie chart
                pie.redraw();
                break;
        }
    }

    /**
     * Updates the graph based on the selected checkboxes for severity and eating.
     *
     * @param v The checkbox view representing the selected graph choice.
     */
    public void onCheckboxGroup(View v) {
        CheckBox cb = (CheckBox) v;
        switch (v.getId()) {
            case R.id.graph_choice_1:
                showSeverity = cb.isChecked();
                // Restart the graph with the updated settings
                restartGraph();
                break;
            case R.id.graph_choice_2:
                showEating = cb.isChecked();
                // Restart the graph with the updated settings
                restartGraph();
                break;
        }
    }

    /**
     * Sets the layout based on the selected graph type.
     *
     * @param graph The graph type to be displayed (PIE_CHART or LINE_PLOT).
     */
    private void setLayout(PatientGraph graph) {
        switch (graph) {
            case PIE_CHART:
                xyChartLayout.setVisibility(View.GONE);
                pieChartLayout.setVisibility(View.VISIBLE);
                break;
            default:
                pieChartLayout.setVisibility(View.GONE);
                xyChartLayout.setVisibility(View.VISIBLE);
                simplePatientXYPlot.setZoomVertically(false);
        }
        // Clear the XY plot
        simplePatientXYPlot.clear();
        // Clear the pie chart
        pie.clear();
    }

    /**
     * A private static nested class used for sorting TimePoint objects based on their time values.
     */
    private static class TimePointSorter implements Comparator<TimePoint> {

        /**
         * Compares two TimePoint objects based on their time values.
         *
         * @param x The first TimePoint object to compare.
         * @param y The second TimePoint object to compare.
         * @return A negative integer, zero, or a positive integer as the first argument
         * is less than, equal to, or greater than the second argument.
         */
        public int compare(TimePoint x, TimePoint y) {
            return Long.compare(x.getTimeValue(), y.getTimeValue());
        }
    }

    /**
     * Resets the counts of severity and eating categories.
     */
    private void resetCounts() {
        notEatingCount = 0;
        eatingSomeCount = 0;
        eatingOkCount = 0;
        severeCount = 0;
        moderateCount = 0;
        controlledCount = 0;
    }

    /**
     * Updates the count for the given severity value.
     *
     * @param value The severity value to be updated.
     */
    private void updateSeverityCounts(int value) {
        if (PainLog.Severity.SEVERE.getValue() == value) {
            severeCount++;
        } else if (PainLog.Severity.MODERATE.getValue() == value) {
            moderateCount++;
        } else if (PainLog.Severity.WELL_CONTROLLED.getValue() == value) {
            controlledCount++;
        }
    }

    /**
     * Updates the count for the given eating value.
     *
     * @param value The eating value to be updated.
     */
    private void updateEatingCounts(int value) {
        if (PainLog.Eating.NOT_EATING.getValue() == value) {
            notEatingCount++;
        } else if (PainLog.Eating.SOME_EATING.getValue() == value) {
            eatingSomeCount++;
        } else if (PainLog.Eating.EATING.getValue() == value) {
            eatingOkCount++;
        }
    }
}
