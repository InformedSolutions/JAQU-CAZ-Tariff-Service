INSERT INTO public.T_CAZ_CLASS (CAZ_CLASS, CAZ_CLASS_DESC)
VALUES ('A', 'Buses, coaches, taxis and private hire vehicles (PHVs)');
INSERT INTO public.T_CAZ_CLASS (CAZ_CLASS, CAZ_CLASS_DESC)
VALUES ('B', 'Buses, coaches, taxis, PHVs and heavy goods vehicles (HGVs)');
INSERT INTO public.T_CAZ_CLASS (CAZ_CLASS, CAZ_CLASS_DESC)
VALUES ('C', 'Buses, coaches, taxis, PHVs, HGVs and light goods vehicles (LGVs)');
INSERT INTO public.T_CAZ_CLASS (CAZ_CLASS, CAZ_CLASS_DESC)
VALUES ('D', 'Buses, coaches, taxis, PHVs, HGVs LGVs and cars');

INSERT INTO public.T_CHARGE_DEFINITION (CHARGE_DEFINITION_ID, CAZ_NAME, CAZ_CLASS,
                                        CLEAN_AIR_ZONE_ID, CHARGE_IDENTIFIER,
                                        ACTIVE_CHARGE_START_TIME, ACTIVE_CHARGE_END_TIME)
VALUES (1, 'Birmingham', 'D', '5cd7441d-766f-48ff-b8ad-1809586fea37', 'BCC01', '2019-08-20',
        '2019-12-20');
INSERT INTO public.T_CHARGE_DEFINITION (CHARGE_DEFINITION_ID, CAZ_NAME, CAZ_CLASS,
                                        CLEAN_AIR_ZONE_ID, CHARGE_IDENTIFIER,
                                        ACTIVE_CHARGE_START_TIME, ACTIVE_CHARGE_END_TIME)
VALUES (2, 'Leeds', 'B', '39e54ed8-3ed2-441d-be3f-38fc9b70c8d3', 'LCC01', '2019-07-20',
        '2019-11-20');

INSERT INTO public.T_TARIFF_DEFINITION (CHARGE_DEFINITION_ID, HGV_ENTRANT_FEE, CAR_ENTRANT_FEE,
                                        MINIBUS_ENTRANT_FEE, TAXI_ENTRANT_FEE, PHV_ENTRANT_FEE,
                                        BUS_ENTRANT_FEE, MOTORCYCLE_ENT_FEE, COACH_ENTRANT_FEE,
                                        VAN_ENTRANT_FEE, MOPED_ENTRANT_FEE)
VALUES (1, '100', '100', '100', '100', '8', '50', '0', '50', '8', '0');
INSERT INTO public.T_TARIFF_DEFINITION (CHARGE_DEFINITION_ID, HGV_ENTRANT_FEE, CAR_ENTRANT_FEE,
                                        MINIBUS_ENTRANT_FEE, TAXI_ENTRANT_FEE, PHV_ENTRANT_FEE,
                                        BUS_ENTRANT_FEE, MOTORCYCLE_ENT_FEE, COACH_ENTRANT_FEE,
                                        VAN_ENTRANT_FEE, MOPED_ENTRANT_FEE)
VALUES (1, '50', '8', '8', '8', '8', '50', '0', '50', '8', '0');
INSERT INTO public.T_TARIFF_DEFINITION (CHARGE_DEFINITION_ID, HGV_ENTRANT_FEE, CAR_ENTRANT_FEE,
                                        MINIBUS_ENTRANT_FEE, TAXI_ENTRANT_FEE, PHV_ENTRANT_FEE,
                                        BUS_ENTRANT_FEE, MOTORCYCLE_ENT_FEE, COACH_ENTRANT_FEE,
                                        VAN_ENTRANT_FEE, MOPED_ENTRANT_FEE)
VALUES (2, '50', '0', '12.5', '12.5', '12.5', '50', '0', '0', '0', '0');

INSERT INTO public.T_CAZ_LINK_DETAIL (CHARGE_DEFINITION_ID,
                                      BOUNDARY_URL,
                                      MAIN_INFO_URL,
                                      EXEMPTION_URL,
                                      BECOME_COMPLIANT_URL,
                                      ADDITIONAL_INFO_URL,
                                      PUBLIC_TRANSPORT_OPTIONS_URL)
VALUES (1,
        'https://www.birmingham.gov.uk/info/20076/pollution/1763/a_clean_air_zone_for_birmingham/3',
        'https://www.birmingham.gov.uk/info/20076/pollution/1763/a_clean_air_zone_for_birmingham',
        'https://exemption.birmingham.gov.uk',
        'https://www.birmingham.gov.uk/info/20076/pollution/1763/a_clean_air_zone_for_birmingham/7',
        'https://www.brumbreathes.co.uk/what-does-it-mean-for-me',
        'https://www.brumbreathes.co.uk/info/15/drive-work-clean-air-zone-1/9/drive-work-clean-air-zone/4');
INSERT INTO public.T_CAZ_LINK_DETAIL (CHARGE_DEFINITION_ID,
                                      BOUNDARY_URL,
                                      MAIN_INFO_URL,
                                      EXEMPTION_URL,
                                      BECOME_COMPLIANT_URL,
                                      ADDITIONAL_INFO_URL,
                                      PUBLIC_TRANSPORT_OPTIONS_URL)
VALUES (2,
        'https://www.arcgis.com/home/webmap/viewer.html?webmap=de0120ae980b473982a3149ab072fdfc&extent=-1.733%2c53.7378%2c-1.333%2c53.8621',
        'https://www.leeds.gov.uk/business/environmental-health-for-business/air-quality',
        'https://exemption.leeds.gov.uk',
        'https://www.leeds.gov.uk/business/environmental-health-for-business/air-quality/exempt-vehicles-clean-air-charging-zone',
        'https://www.leeds.gov.uk/business/environmental-health-for-business/air-quality?utm_source=Clean_Air_Leeds_Website&utm_medium=referral&utm_term=What_Are_We_Doing',
        'https://cleanairleeds.co.uk/what-can-i-do/residents');