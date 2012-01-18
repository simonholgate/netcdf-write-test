(ns netcdf_write_test.core)

(import '(ucar.nc2 NetcdfFileWriteable Dimension)
        '(ucar.ma2 DataType ArrayFloat InvalidRangeException)
        '(java.util ArrayList)
        '(java.io IOException))

(defn create-coord [n start step]
  (map #(float(+ (* %2 %3) %1)) (repeat n start) (take n (iterate inc 0))
       (repeat n step)))

(defn make-coord [n start]
  (let [step 5]
    (ArrayFloat/factory 
      (float-array n (create-coord n start step)))))

(defn i-seq [ nlon nlat ]
  (flatten (map #(repeat nlon %1) (range 0 nlat))))

(defn j-seq [ nlon nlat ]
  (flatten (repeat nlat (range 0 nlon))))

(defn create-temp-array [i n m sample]
  ;; Creates a float array of synthetic temperatures n long
  (float-array 
    (map #(+ sample (* 0.25 (+ i (* % m)))) (range 0 n))))

(defn create-pres-array [i n m sample]
  ;; Creates a float array of synthetic pressures n long
  (float-array 
    (map #(+ sample (+ i (* % m))) (range 0 n))))

(defn create-data [nlon nlat sample type]
  (if (= "temp" type)
    (ArrayFloat/factory (into-array 
      (map #(create-temp-array % nlon nlat sample) (range 0 nlat))))
    (ArrayFloat/factory (into-array 
      (map #(create-pres-array % nlon nlat sample) (range 0 nlat))))))

(defn make-dims [lat-dim lon-dim]
  (doto (ArrayList.)
    (.add lat-dim)
    (.add lon-dim)))

(defn set-coord [x y z]
  (.setFloat x y (float z)))

(defn data-file [filename]
  (NetcdfFileWriteable. filename false))

(defn make-float-array [n]
  (ArrayFloat. (int-array 1 n)))

(defn make-lat-data [x NLAT START-LAT]
  (make-coord x NLAT START-LAT))

(defn make-file [datafile]
  (let [NLAT 6
        NLON 12
        SAMPLE-PRESSURE 900.0
        SAMPLE-TEMP 9.0
        START-LAT 25.0
        START-LON -125.0      
        ;; Create new netcdf-3 file with the given filename
        ;;data-file (NetcdfFileWriteable. filename false)
        origin (int-array 2)
        ;;
        ;; In addition to the latitude and longitude dimensions, we will
        ;; also create latitude and longitude netCDF variables which will
        ;; hold the actual latitudes and longitudes. Since they hold data
        ;; about the coordinate system, the netCDF term for these is:
        ;; "coordinate variables."
        ;;
        lat-dim (.addDimension datafile "latitude" NLAT)
        lon-dim (.addDimension datafile "longitude" NLON)
        dims (make-dims lat-dim lon-dim)
        ;; Create the coordinate data
        data-lat (make-coord NLAT START-LAT)
        data-lon (make-coord NLON START-LON)       
        ;; Create some pretend data. In a real program we would have
        ;; model output or data from a database
        data-temp (create-data NLON NLAT SAMPLE-TEMP "temp")
        data-pres (create-data NLON NLAT SAMPLE-PRESSURE "pres")]    
    ;; 
    (doto datafile
      (.addVariable "latitude" (DataType/FLOAT) 
                    (into-array Dimension [lat-dim]))
      (.addVariable "longitude" (DataType/FLOAT) 
                    (into-array Dimension [lon-dim]))
      (.addVariable "pressure" (DataType/FLOAT) dims)
      (.addVariable "temperature" (DataType/FLOAT) dims)
    ;;
    ;; Define units attributes for coordinate vars. This attaches a
    ;; text attribute to each of the coordinate variables, containing
    ;; the units.
      (.addVariableAttribute "longitude" "units" "degrees_east")
      (.addVariableAttribute "latitude" "units" "degrees_north")
      (.addVariableAttribute "pressure" "units" "hPa")
      (.addVariableAttribute "temperature" "units" "celsius")
    ;;
    ;; Write the coordinate variable data. This will put the latitudes
    ;; and longitudes of our data grid into the netCDF file.
    ;;
      (.create)
      ;;
      ;; Actually write the data to the file
      ;;
      (.write "latitude" data-lat)
      (.write "longitude" data-lon)
      (.write "pressure" origin data-pres)
      (.write "temperature" origin data-temp)
    (.close))
    (println "Closed file - SUCCESS!")))



