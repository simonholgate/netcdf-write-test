(ns netcdf_write_test.main
  (:gen-class))
(import '(ucar.nc2 NetcdfFileWriteable Dimension)
        '(ucar.ma2 DataType ArrayFloat InvalidRangeException)
        '(java.util ArrayList)
        '(java.io IOException))
(use 'netcdf_write_test.core)
(defn -main []
  "Run the main program"
  (let [filename  "/store/work/simonh/bin/clojure/sfc_pres_temp.nc"]
    (make-file (data-file filename))))