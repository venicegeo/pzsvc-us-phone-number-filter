package us.phone.number.filter


import groovy.json.JsonOutput


class FilterController {

	def grailsApplication
	def phoneNumberFilterService


	def index() {
		grailsApplication.config.app.count++	
 
		def map = phoneNumberFilterService.serviceMethod(params, request)
		def json = new JsonOutput().toJson(map)


		response.contentType = "application/json"
		render json
	}
}
