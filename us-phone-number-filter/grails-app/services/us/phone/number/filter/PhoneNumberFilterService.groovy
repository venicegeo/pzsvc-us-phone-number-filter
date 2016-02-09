package us.phone.number.filter


import groovy.json.JsonSlurper


class PhoneNumberFilterService {


	def checkForPhoneNumbers(object) {
		def pattern = /([^0-9]+[(]?[2-9]\d{2}[)]?|^[(]?[2-9]\d{2}[)]?)(\s|-|[.])[2-9]\d{2}(\s|-|[.])(\d{4}[^0-9]+|\d{4}$)/

		
		if (object.toString().find(pattern)) { return true }
		else { return false }
	}

	def serviceMethod(params, request) {
		// Get the submitted json
		def json = request.JSON
		if (json.isEmpty()) {
			def text = params.find { true }.key 
			// For GET JSON 
			try { json = new JsonSlurper().parseText(text) }
			// For GET TEXT
			catch (Exception e) { json = text }
		}

		// Iterate through the JSON applying the boundary filter
		def failed = []
		def passed = []
		def jsonClass = json.getClass().toString()
		if (jsonClass.contains("Object") || jsonClass.contains("Map")) {
			// Support GeoJSON
			if (json["features"]) {
				json.features.each() {
					if (checkForPhoneNumbers(it)) { failed.push(it) }
					else { passed.push(it) } 
				}
				
				json.features = []
				def filterMap =  [
					failed: new LinkedHashMap(json),
					passed: new LinkedHashMap(json) 
				]
				filterMap.passed.features = passed
				filterMap.failed.features = failed


				return filterMap
			}
			// Support regular JSON
			else { 
				if (checkForPhoneNumbers(json)) { failed.push(json) }
				else { passed.push(json) }


				return [passed: passed, failed: failed]
			}
		}
		else if (jsonClass.contains("Array")) { 
			json.each() {
					if (checkForPhoneNumbers(it)) { failed.push(it) }
					else { passed.push(it) }
			}

		
			return [failed: failed, passed: passed]
		}
		else {
			if (checkForPhoneNumbers(json)) { failed.push(json) }
			else { passed.push(json) }


			return [passed: passed, failed: failed]
		}
	}
}
