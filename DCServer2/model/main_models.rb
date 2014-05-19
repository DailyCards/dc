module DC

	class User 
		include DataMapper::Resource
		property :id , Serial ,:key=>true
		property :name , String, :required => true
		property :password , String, :required => true
		property :realname , String, :required => true
		property :phoneno , String
		
		
	end

	class Carrier
		include DataMapper::Resource
		property :id , Serial ,:key=>true
		property :name , String, :required => true
		property :description, Text, :lazy => false
		property :phoneno , String
		has n ,:campaigns
		
	end
	
	class Campaign
		include DataMapper::Resource
		property :id , Serial ,:key=>true
		property :name , String, :required => true
		property :description, Text, :lazy => false
		property :startdate , DateTime
		property :enddate , DateTime
		belongs_to :carrier
	end
	
	class Prize
		include DataMapper::Resource
		property :id , Serial ,:key=>true
		property :name , String, :required => true
		property :description, Text, :lazy => false
		belongs_to :campaign
	end
	
	class PrizeConfig
		include DataMapper::Resource
		property :id , Serial ,:key=>true
		property :name , String, :required => true
		property :description, Text, :lazy => false
		property :amount , Integer 
		belongs_to :campaign
	end
	 
  
end
