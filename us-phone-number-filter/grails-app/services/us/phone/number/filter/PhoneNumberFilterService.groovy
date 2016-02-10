package us.phone.number.filter


import groovy.json.JsonSlurper


class PhoneNumberFilterService {


	def checkForPhoneNumbers(object) {
		def pattern = /([^0-9]+[(]?[2-9]\d{2}[)]?|^[(]?[2-9]\d{2}[)]?)(\s|-|[.])[2-9]\d{2}(\s|-|[.])(\d{4}[^0-9]+|\d{4}$)/

		def phoneNumber = object.toString().find(pattern)
		if (phoneNumber) { 
			def areaCode = phoneNumber.find(/[2-9]\d{2}/) as Integer
			def areaCodes = getAreaCodes()
			

			if (areaCodes.find({ it == areaCode })) { return true } 
		}
		else { return false }
	}

	def getAreaCodes() {
		def areaCodes = [
			205, 251, 256, 334, 938, // Alabama
			907, 250, // Alaska
			480, 520, 602, 623, 928, // Arizona
			327, 479, 501, 870, // Arkansas
			209, 213, 310, 323, 408, 415, 424, 442, 510, 530, 559, 562, 619, 626, 628, 650, 657, 661, 669, 707, 714, 747, 760, 805, 818, 831, 858, 909, 916, 925, 949, 951, // California
			303, 719, 720, 970, // Colorado
			203, 475, 860, 959, // Connecticut
			302, // Deleware
			202, // District of Columbia
			239, 305, 321, 352, 386, 407, 561, 727, 754, 772, 786, 813, 850, 863, 904, 941, 954, // Florida
			229, 404, 470, 478, 678, 706, 762, 770, 912, // Georgia
			808, // Hawaii
			208, 986, // Idaho
			217, 224, 309, 312, 331, 447, 464, 618, 630, 708, 730, 773, 779, 815, 847, 872, // Illinois
			219, 260, 317, 463, 574, 765, 812, 930, // Indiana
			319, 515, 563, 641, 712, // Iowa
			316, 620, 785, 913, // Kansas
			270, 364, 502, 606, 859, // Kentucky
			225, 318, 337, 504, 985, // Louisiana
			207, // Maine
			227, 240, 301, 410, 443, 667, // Maryland
			339, 351, 413, 508, 617, 774, 781, 857, 978, // Massachusetts
			231, 248, 269, 313, 517, 586, 616, 734, 810, 906, 947, 989, // Michigan
			218, 320, 507, 612, 651, 763, 952, // Minesota
			228, 601, 662, 769, // Mississippi
			314, 417, 573, 636, 660, 816, 975, // Missouri
			406, // Montana
			308, 402, 531, // Nebraska
			702, 725, 775, // Nevada
			603, // New Hampshire
			201, 551, 609, 732, 848, 856, 862, 908, 973, // New Jersey
			505, 575, // New Mexico
			212, 315, 332, 347, 516, 518, 585, 607, 631, 646, 680, 716, 718, 845, 914, 917, 929, 934, // New York
			252, 336, 704, 743, 828, 910, 919, 980, 984, // North Carolina
			701, // North Dakota
			216, 220, 234, 283, 330, 380, 419, 440, 513, 567, 614, 740, 937, // Ohio
			405, 539, 580, 918, // Oklahoma
			458, 503, 541, 971, // Oregon
			215, 267, 272, 412, 484, 570, 610, 717, 724, 814, 878, // Pennsylvania
			401, // Rhode Island
			803, 843, 854, 864, // South Carolina
			605, // South Dakota
			423, 615, 629, 731, 865, 901, 931, // Tennessee
			210, 214, 254, 281, 325, 346, 361, 409, 430, 432, 469, 512, 682, 713, 737, 806, 817, 830, 832, 903, 915, 936, 940, 956, 972, 979, // Texas
			385, 435, 801, // Utah
			802, // Vermont
			276, 434, 540, 571, 703, 757, 804, // Virginia
			206, 253, 360, 425, 509, 564, // Washington
			304, 681, // West Virginia
			262, 274, 414, 534, 608, 715, 920, // Wisconsin
			307 // Wyoming
		]


		return areaCodes
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
					if (checkForPhoneNumbers(it.properties.toString())) { failed.push(it) }
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
