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
                                        ACTIVE_CHARGE_START_TIME, ACTIVE_CHARGE_END_TIME,
                                        ACTIVE_CHARGE_START_DATE_TEXT,CAZ_OPERATOR_NAME,
                                        DISPLAY_FROM, DISPLAY_ORDER, DIRECT_DEBIT_START_DATE_TEXT)
VALUES (1, 'Birmingham', 'D', '5cd7441d-766f-48ff-b8ad-1809586fea37', 'BCC01', '2021-06-01',
        '2021-12-20', '1 June 2021', 'Birmingham City Council', '2021-01-01', 2, '4 May 2021');

INSERT INTO public.T_CHARGE_DEFINITION (CHARGE_DEFINITION_ID, CAZ_NAME, CAZ_CLASS,
                                        CLEAN_AIR_ZONE_ID, CHARGE_IDENTIFIER,
                                        ACTIVE_CHARGE_START_TIME, ACTIVE_CHARGE_END_TIME,
                                        CAZ_OPERATOR_NAME, DISPLAY_FROM, DISPLAY_ORDER,
                                        DIRECT_DEBIT_START_DATE_TEXT)
VALUES (2, 'Bath', 'C', '5dd5c926-ed33-4a0a-b911-46324433e866', 'BAT01', '2021-03-15',
        '2021-11-20', 'Bath and North East Somerset Council', '2021-01-01', 1, '4 May 2021');

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
                                      PAYMENTS_COMPLIANCE_URL,
                                      FLEETS_COMPLIANCE_URL,
                                      PUBLIC_TRANSPORT_OPTIONS_URL,
                                      PRIVACY_POLICY_URL)
VALUES (1,
        'https://www.birmingham.gov.uk/info/20076/pollution/1763/a_clean_air_zone_for_birmingham/3',
        'https://www.birmingham.gov.uk/info/20076/pollution/1763/a_clean_air_zone_for_birmingham',
        'https://exemption.birmingham.gov.uk',
        'https://www.birmingham.gov.uk/info/20076/pollution/1763/a_clean_air_zone_for_birmingham/7',
        'https://www.brumbreathes.co.uk/homepage/7/financial-incentive',
        'https://businessbreathes.co.uk/',
        'https://www.brumbreathes.co.uk/info/15/drive-work-clean-air-zone-1/9/drive-work-clean-air-zone/4',
        'https://www.birmingham.gov.uk/info/20015/environment/2005/privacy_statement_-_environmental_health/2');
INSERT INTO public.T_CAZ_LINK_DETAIL (CHARGE_DEFINITION_ID,
                                      BOUNDARY_URL,
                                      MAIN_INFO_URL,
                                      EXEMPTION_URL,
                                      BECOME_COMPLIANT_URL,
                                      PAYMENTS_COMPLIANCE_URL,
                                      FLEETS_COMPLIANCE_URL,
                                      PUBLIC_TRANSPORT_OPTIONS_URL,
                                      PRIVACY_POLICY_URL)
VALUES (2,
        'http://www.bathnes.gov.uk/zonemaps',
        'http://www.bathnes.gov.uk/BathCAZ',
        'http://www.bathnes.gov.uk/CAZexemptions',
        'http://www.bathnes.gov.uk/CAZsupport',
        'http://www.bathnes.gov.uk/CAZsupport',
        'http://www.bathnes.gov.uk/CAZsupport',
        'http://www.bathnes.gov.uk/cleanair4bathnes',
        'https://beta.bathnes.gov.uk/council-privacy-policy');