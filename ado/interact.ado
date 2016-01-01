capture program drop interact
program define interact, nclass
  version 13.0
  syntax , [path(string)] [mode(string)] [sleep(integer 200)] [reset]

set more off

cap _return drop interactReturnHold
_return hold interactReturnHold

if ( "`path'" == "" ) {
	tempfile locate
	local path = substr("`locate'",1,length("`locate'")-15)+"/"
} 
else {
    // make sure it is a proper path
    local path = "`path'/"
}

if ( "`mode'" == "" ) {
	local mode = "smcl"
}


// markers
local domarker = "`path'do.marker"
local endmarker = "`path'end.marker"

// to temporary to file
local dofile = "`path'i.do"

// the log file
local logfile = "`path'i.log"

// the stealth log file
local stealthfile = "`path'stealth.log"

// the file to hold the current description of the variables
local descfile = "`path'describe.log"

// the file to hold the last graph and the corresponding log file
local graphlogfile = "`path'graph.log"
local graphfile = "`path'graph.png"

// the file to hold the last estimates and the corresponding log file
local estlogfile = "`path'est.log"
local estfile = "`path'est.html"

//
// check that files are deleted
//
capture erase "`logfile'"
capture erase "`stealthfile'"
capture erase "`endmarker'"

//
// ensure log files are closed
//
cap qui log close _interact_do
cap qui log close _interact_describe
cap qui log close _interact_graph
cap qui log close _interact_est
cap qui log close _interact_stealth

// alive file: an indication if the service is running
local alivemarker = "`path'alive.marker"

//
// clear the alive marker and do file/marker
//
if ( "`reset'" != "" ) {
  di "reset"
  capture erase "`dofile'"
  capture erase "`alivemarker'"
  capture erase "`domarker'"
}

//
// set as alive
//
file open alive using "`alivemarker'", write
file write alive "alive"
file close alive


di as result ""
di "Setup for marker in file: `domarker' ...  (hit cancel to exit)"
di ""
di "... " _continue

//
// MAIN LOOP
//
capture noisily while ( _rc != 1 ) {
    
    //
    // check if the next do file exists
    //
    capture confirm file "`domarker'"
    if ( _rc == 601 ) {
        di as result "waiting for marker in file: `domarker' ...  (hit cancel to exit)"
        di "... " _continue
    }
    while (_rc == 601) {
        sleep `sleep'
        capture confirm file "`domarker'"
    }
    
	file open domarker using "`domarker'", read
	file read domarker command
	file read domarker option
	file close domarker

	// 
	// check if the receiver has deleted the end marker 
	// otherwise wait 
	// TODO (this should timeout!)
	//
	capture confirm file "`endmarker'"
	while (_rc == 0 & _rc != 601) {
		sleep `sleep'
		capture confirm file "`endmarker'"
	}

	sleep 10
	
    di as result "start `command' ..."
    di ""
	if ( "`command'" == "do" ) {
	
		qui log using "`logfile'", append `mode' name(_interact_do)
		qui _return restore interactReturnHold
		capture noisily do "`dofile'"
		qui _return hold interactReturnHold
		qui log close _interact_do
		
		//
		// delete the do file to make space for the next one
		//
		erase "`dofile'"
		
	} 
	else if ( "`command'" == "stealth" ) {
	
		qui log using "`stealthfile'", append `mode' name(_interact_stealth)
		qui _return restore interactReturnHold
		capture noisily do "`dofile'"
		qui _return hold interactReturnHold
		qui log close _interact_stealth
		
		//
		// delete the do file to make space for the next one
		//
		erase "`dofile'"
		
	} 
	else if ( "`command'" == "describe" ){
		
	    qui log using "`descfile'", replace text name(_interact_describe)
	    describe
		qui graph dir, memory
		di "MEMORY GRAPHS: `r{list)'"
	    pwd
	    qui log close _interact_describe
		
	}
	else if ( "`command'" == "graph" ){
		
	    qui log using "`graphlogfile'", replace text name(_interact_graph)
	    cap {
			// TODO graph dir
			noi graph export "`graphfile'", replace name(`option')
		}
	    qui log close _interact_graph
		
	}
	else if ( "`command'" == "est" ){
		
	    qui log using "`estlogfile'", replace text name(_interact_est)
	    cap local est_scalars: e(scalars)
	    cap noi esttab using "`estfile'", html replace scalars(`est_scalars')
	    qui log close _interact_est
		
	}
	else if ( "`command'" == "clear" ){
		
        clear

		capture erase "`dofile'"
        
		qui log using "`logfile'", replace `mode' name(_interact_do)
		qui log close _interact_do

	    qui log using "`descfile'", replace text name(_interact_describe)
	    qui log close _interact_describe
		
	    qui log using "`graphlogfile'", replace text name(_interact_graph)
	    qui log close _interact_graph
		
	}
    di as result "... finished `command' ..."
	
	erase "`domarker'"
	
	file open marker using "`endmarker'", write
	file write marker "end "
	file close marker

    di as result "... " _continue

}

_return drop interactReturnHold

//
// clear the do file
//
capture erase "`dofile'"
capture erase "`alivemarker'"
capture erase "`domarker'"
capture erase "`endmarker'"


// clean up
di "canceled interact"

end

// eof
