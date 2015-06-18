package com.weproov.app.models;

import com.weproov.app.R;

public class Country {

	public String name;
	public String ISO3166;
	public int phone;
	public int order;
	public int res;

	public Country(String name, String ISO3166, int phone, int order, int res) {
		this.name = name;
		this.ISO3166 = ISO3166;
		this.phone = phone;
		this.order = order;
		this.res = res;
	}


	public static final Country[] COUNTRIES = new Country[]{
			new Country("Belgium", "be", 32, 0, R.drawable.be),
			new Country("France", "fr", 33, 0, R.drawable.fr),
			new Country("Germany", "de", 49, 0, R.drawable.de),
			new Country("Italy", "it", 39, 0, R.drawable.it),
			new Country("Spain", "es", 34, 0, R.drawable.es),
			new Country("United Kingdom", "gb", 44, 0, R.drawable.gb),
			new Country("United States", "us", 1, 0, R.drawable.us),
	};
}
