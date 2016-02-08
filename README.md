# pzsvc-us-phone-number-filter
Filters data based on whether it contains US phone numbers.


## Requirements
* Vagrant 1.8.1


## Example Usage
This service accepts `GET` and `POST` requests taking an array and returning a JSON containing two arrays, one which are those that "passed" the filter (i.e. it does NOT contain a US phone number) and the other that "failed". <br>
`http://localhost:8080/filter?<geojson>`
<br> OR <br>
`http://localhost:8080/filter?[{"xxx-xxx-xxxx","1-(xxx)-xxx-xxxx"]`
<br> OR <br>
`http://localhost:8080/filter?xxx.xxx.xxxx`


## Service Standup
1. `vagrant up`.
2. `vagrant ssh`.
3. `cd sync/us-geospatial-filter`.
4. `grails run-app`.
