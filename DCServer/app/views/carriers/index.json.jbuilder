json.array!(@carriers) do |carrier|
  json.extract! carrier, :id
  json.url carrier_url(carrier, format: :json)
end
