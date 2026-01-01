package com.example.florra_a;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CalculatorActivity extends AppCompatActivity {

    private EditText etLength, etWidth;
    private Spinner spinnerTileSize;
    private SeekBar seekBarWastage;
    private TextView tvWastagePercent, tvTotalBoxes, tvCoverageArea, tvTileCount;
    private RadioGroup rgUnits;
    private Button btnSaveCalculation, btnReset;
    private ImageButton btnBack;

    // Tile sizes in square feet
    private static final double TILE_600x600 = 2.0 * 2.0;      // 4 sq ft
    private static final double TILE_600x1200 = 2.0 * 4.0;     // 8 sq ft
    private static final double TILE_800x800 = 2.63 * 2.63;    // ~6.9 sq ft
    private static final double TILE_1200x1200 = 4.0 * 4.0;    // 16 sq ft

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_calculator);

        initializeViews();
        setupSpinner();
        setupListeners();
        setupSeekBar();

        // Set initial values
        updateResults();
    }

    private void initializeViews() {
        etLength = findViewById(R.id.etLength);
        etWidth = findViewById(R.id.etWidth);
        spinnerTileSize = findViewById(R.id.spinnerTileSize);
        seekBarWastage = findViewById(R.id.seekBarWastage);
        tvWastagePercent = findViewById(R.id.tvWastagePercent);
        tvTotalBoxes = findViewById(R.id.tvTotalBoxes);
        tvCoverageArea = findViewById(R.id.tvCoverageArea);
        tvTileCount = findViewById(R.id.tvTileCount);
        rgUnits = findViewById(R.id.rgUnits);
        btnSaveCalculation = findViewById(R.id.btnSaveCalculation);
        btnBack = findViewById(R.id.btnBack);
        btnReset = findViewById(R.id.btnReset);
    }

    private void setupSpinner() {
        // Create adapter for tile sizes
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.tile_sizes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTileSize.setAdapter(adapter);

        // Set default selection (600x600)
        spinnerTileSize.setSelection(1);

        // Add spinner change listener
        spinnerTileSize.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                updateResults();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });
    }

    private void setupListeners() {
        // Back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Reset button
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetCalculator();
            }
        });

        // Save Calculation button
        btnSaveCalculation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCalculation();
            }
        });

        // Unit selection
        rgUnits.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                updateUnitLabels();
                updateResults();
            }
        });

        // Text change listeners for real-time calculation
        etLength.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                updateResults();
            }
        });

        etWidth.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                updateResults();
            }
        });
    }

    private void setupSeekBar() {
        seekBarWastage.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvWastagePercent.setText(progress + "%");
                updateResults();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void updateUnitLabels() {
        String unit = "ft";
        int checkedId = rgUnits.getCheckedRadioButtonId();

        if (checkedId == R.id.rbMeters) {
            unit = "m";
        } else if (checkedId == R.id.rbInches) {
            unit = "in";
        }

        // Update unit labels in the layout
        TextView tvLengthUnit = findViewById(R.id.tvLengthUnit);
        TextView tvWidthUnit = findViewById(R.id.tvWidthUnit);

        if (tvLengthUnit != null) tvLengthUnit.setText(unit);
        if (tvWidthUnit != null) tvWidthUnit.setText(unit);
    }

    private void updateResults() {
        try {
            // Get inputs
            double length = etLength.getText().toString().isEmpty() ? 0 :
                    Double.parseDouble(etLength.getText().toString());
            double width = etWidth.getText().toString().isEmpty() ? 0 :
                    Double.parseDouble(etWidth.getText().toString());
            int wastage = seekBarWastage.getProgress();

            // Get selected unit
            int checkedId = rgUnits.getCheckedRadioButtonId();

            // Convert to feet (base unit for calculation)
            if (checkedId == R.id.rbMeters) {
                // Convert meters to feet (1 meter = 3.28084 feet)
                length = length * 3.28084;
                width = width * 3.28084;
            } else if (checkedId == R.id.rbInches) {
                // Convert inches to feet (1 inch = 0.0833333 feet)
                length = length * 0.0833333;
                width = width * 0.0833333;
            }
            // If feet, no conversion needed

            // Calculate area in square feet
            double area = length * width;

            // Get selected tile size
            int tileSizeIndex = spinnerTileSize.getSelectedItemPosition();
            double tileArea = 0;

            // Set tile area based on selection
            switch (tileSizeIndex) {
                case 1: // 600 x 600 mm (2x2 ft)
                    tileArea = TILE_600x600; // 4 sq ft
                    break;
                case 2: // 600 x 1200 mm (2x4 ft)
                    tileArea = TILE_600x1200; // 8 sq ft
                    break;
                case 3: // 800 x 800 mm
                    tileArea = TILE_800x800; // ~6.9 sq ft
                    break;
                case 4: // 1200 x 1200 mm
                    tileArea = TILE_1200x1200; // 16 sq ft
                    break;
                case 5: // Custom Dimensions (use default 600x600)
                    tileArea = TILE_600x600; // 4 sq ft
                    break;
                default: // Choose a format... (use default 600x600)
                    tileArea = TILE_600x600; // 4 sq ft
                    break;
            }

            // Calculate number of tiles needed (without wastage)
            double tilesNeeded = area / tileArea;

            // Apply wastage factor
            double wastageFactor = 1 + (wastage / 100.0);
            double totalTiles = Math.ceil(tilesNeeded * wastageFactor);

            // Calculate boxes (assuming 10 tiles per box)
            double tilesPerBox = 10;
            double totalBoxes = Math.ceil(totalTiles / tilesPerBox);

            // Update UI with REAL calculated values
            tvTotalBoxes.setText(String.valueOf((int)totalBoxes));
            tvCoverageArea.setText(String.format("%.1f sq. ft", area));
            tvTileCount.setText(String.format("~%.0f Tiles", totalTiles));

        } catch (Exception e) {
            // Reset to defaults if error
            tvTotalBoxes.setText("0");
            tvCoverageArea.setText("0 sq. ft");
            tvTileCount.setText("~0 Tiles");
        }
    }

    private void resetCalculator() {
        etLength.setText("");
        etWidth.setText("");
        spinnerTileSize.setSelection(0);
        seekBarWastage.setProgress(5);
        updateResults();
        Toast.makeText(this, "Calculator reset", Toast.LENGTH_SHORT).show();
    }

    private void saveCalculation() {
        Toast.makeText(this, "Calculation saved", Toast.LENGTH_SHORT).show();
        // Here you would implement the logic to save calculation
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}