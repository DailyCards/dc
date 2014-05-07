class CreateCampaigns < ActiveRecord::Migration
  def change
    create_table :campaigns do |t|
      t.references :carrier
      t.string :name
      t.string :description
      t.date :start_date
      t.date :end_date
      t.timestamps
    end
  end
end
