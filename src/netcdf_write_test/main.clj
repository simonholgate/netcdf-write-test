(ns netcdf_write_test.main
  (:gen-class))
(import '(ucar.nc2 NetcdfFileWriteable Dimension)
        '(ucar.ma2 DataType ArrayFloat InvalidRangeException)
        '(java.util ArrayList)
        '(java.io IOException))
(use 'netcdf_write_test.core)
(defn -main [filename]
  "Run the main program"
    (make-file (data-file filename)))